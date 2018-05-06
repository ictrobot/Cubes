package ethanjones.cubes.graphics.world.ao;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import java.io.File;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static ethanjones.cubes.graphics.world.ao.AmbientOcclusion.*;

public class AOTextureGenerator {

  private static final int SIGMA = INDIVIDUAL_SIZE / 8;
  private static final double[][] gwm = AOTextureGenerator.gaussianWeightMatrix(SIGMA);
  private static final int gaussianRadius = (gwm.length - 1) / 2;
  private static ScheduledThreadPoolExecutor executor;
  private static final ThreadLocal<Pixmap> gaussianLocal = new ThreadLocal<Pixmap>() {
    @Override
    protected Pixmap initialValue() {
      return new Pixmap(INDIVIDUAL_SIZE, INDIVIDUAL_SIZE, FORMAT);
    }
  };
  private static final ThreadLocal<double[][]> gaussianDLocal = new ThreadLocal<double[][]>() {

    @Override
    protected double[][] initialValue() {
      Pixmap pixmap = gaussianLocal.get();
      return new double[pixmap.getWidth()][pixmap.getHeight()];
    }
  };

  private static void startExecutor() {
    if (executor == null) {
      executor = new ScheduledThreadPoolExecutor(10, new ThreadFactory() {
        int threads = 0;

        public Thread newThread(Runnable r) {
          Thread thread = new Thread(r);
          thread.setName("AO-" + threads++);
          thread.setDaemon(true);
          return thread;
        }
      });
      executor.prestartAllCoreThreads();
    }
  }

  protected static void generate(FileHandle file) {
    final long startTime = System.currentTimeMillis();
    startExecutor();
    Pixmap weak = generate(0.5f);
    Pixmap strong = generate(0.1f);

    Pixmap out = new Pixmap(weak.getWidth(), weak.getHeight(), weak.getFormat());
    Color outColor = new Color();
    Color tempColor = new Color();
    for (int x = 0; x < out.getWidth(); x++) {
      for (int y = 0; y < out.getHeight(); y++) {
        outColor.r = tempColor.set(weak.getPixel(x, y)).r;
        outColor.g = tempColor.set(strong.getPixel(x, y)).r;
        outColor.b = 1f;
        outColor.a = 1f;
        out.drawPixel(x, y, Color.rgba8888(outColor));
      }
    }
    weak.dispose();
    strong.dispose();

    System.out.println("Took " + ((System.currentTimeMillis() - startTime) / 1000 / 60) + " minutes");
    System.out.println("Writing to: " + file.path());
    System.out.println();

    PixmapIO.writePNG(file, out);
    out.dispose();
  }

  private static Pixmap generate(final float strength) {
    System.out.println("Generating AO Texture");

    final Pixmap output = new Pixmap(TEXTURE_SIZE, TEXTURE_SIZE, FORMAT);
    final AtomicInteger done = new AtomicInteger();
    final CountDownLatch initialCountdown = new CountDownLatch(8);
    final double[][][] preCalculated = new double[8][][];

    System.out.println("Generating Base Gaussian Data");
    for (int i = 1, j = 0; i < TOTAL; i <<= 1, j += 1) {
      final int finalI = i, finalJ = j;
      executor.execute(new Runnable() {
        @Override
        public void run() {
          Pixmap blockPixmap = new Pixmap(INDIVIDUAL_SIZE * 3, INDIVIDUAL_SIZE * 3, FORMAT);
          Pixmap gaussianPixmap = gaussianLocal.get();
          double[][] gaussianData = new double[gaussianPixmap.getWidth()][gaussianPixmap.getHeight()];

          blockPixmap.setColor(Color.WHITE);
          blockPixmap.fill();

          setupPixmap(blockPixmap, finalI, new Color(strength, strength, strength, 1f));
          gaussianPixmap(blockPixmap, gaussianData);
          doubleToPixmap(gaussianData, gaussianPixmap);

          synchronized (output) {
            output.drawPixmap(gaussianPixmap, (finalI % SQRT_TOTAL) * INDIVIDUAL_SIZE, (finalI / SQRT_TOTAL) * INDIVIDUAL_SIZE, 0, 0, INDIVIDUAL_SIZE, INDIVIDUAL_SIZE);
          }

          initialCountdown.countDown();
          done.incrementAndGet();

          synchronized (preCalculated) {
            preCalculated[finalJ] = gaussianData;
          }

          blockPixmap.dispose();
        }
      });
    }

    try {
      initialCountdown.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }
    System.out.println("Generated Base Data");
    final CountDownLatch mainCountdown = new CountDownLatch(TOTAL - done.get());

    for (int i = 0; i < TOTAL; i++) {
      if (Long.bitCount(i) == 1) continue;

      final int finalI = i;
      executor.execute(new Runnable() {
        @Override
        public void run() {
          long l = System.currentTimeMillis();

          Pixmap gaussianPixmap = gaussianLocal.get();

          double[][] data = gaussianDLocal.get();
          preCalcGaussianPixmap(data, finalI, preCalculated);
          doubleToPixmap(data, gaussianPixmap);

          synchronized (output) {
            output.drawPixmap(gaussianPixmap, (finalI % SQRT_TOTAL) * INDIVIDUAL_SIZE, (finalI / SQRT_TOTAL) * INDIVIDUAL_SIZE, 0, 0, INDIVIDUAL_SIZE, INDIVIDUAL_SIZE);
          }

          l = System.currentTimeMillis() - l;
          int d = done.incrementAndGet();
          System.out.println(d + "/" + TOTAL + ": " + name(finalI) + " (" + l + "ms)");

          mainCountdown.countDown();
        }
      });
    }

    try {
      mainCountdown.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
      System.exit(1);
    }

    return output;
  }

  private static void gaussianPixmap(Pixmap in, double[][] doubleOut) {
    double[][] doubleIn = new double[in.getWidth()][in.getHeight()];

    pixmapToDouble(in, doubleIn);

    for (int i = 0; i < doubleOut.length; i++) {
      for (int j = 0; j < doubleOut[i].length; j++) {
        doubleOut[i][j] = 0;
      }
    }

    Pixmap out = gaussianLocal.get();

    int offsetX = (in.getWidth() - out.getWidth()) / 2;
    int offsetY = (in.getHeight() - out.getHeight()) / 2;

    for (int x = 0; x < out.getWidth(); x++) {
      for (int y = 0; y < out.getHeight(); y++) {
        double d = 0;

        for (int ox = -gaussianRadius; ox <= gaussianRadius; ox++) {
          for (int oy = -gaussianRadius; oy <= gaussianRadius; oy++) {
            d += doubleIn[x + ox + offsetX][y + oy + offsetY] * gwm[ox + gaussianRadius][oy + gaussianRadius];
          }
        }

        doubleOut[x][y] = d;
      }
    }
  }

  private static void pixmapToDouble(Pixmap in, double[][] out) {
    Color c = new Color();
    for (int x = 0; x < in.getWidth(); x++) {
      for (int y = 0; y < in.getHeight(); y++) {
        c.set(in.getPixel(x, y));
        out[x][y] = c.r;
      }
    }
  }

  private static void doubleToPixmap(double[][] in, Pixmap out) {
    Color c = new Color(0, 0, 0, 1);
    for (int x = 0; x < out.getWidth(); x++) {
      for (int y = 0; y < out.getHeight(); y++) {
        float d = (float) in[x][y];
        c.r = d;
        c.g = d;
        c.b = d;
        c.clamp();
        out.drawPixel(x, y, Color.rgba8888(c));
      }
    }
  }

  private static void apply(double[][] output, double[][] preCalculated) {
    for (int x = 0; x < output.length; x++) {
      for (int y = 0; y < output[x].length; y++) {
        output[x][y] -= (1 - preCalculated[x][y]);
      }
    }
  }


  private static void preCalcGaussianPixmap(double[][] data, int i, double[][][] preCalculated) {
    for (int x = 0; x < data.length; x++) {
      for (int y = 0; y < data[x].length; y++) {
        data[x][y] = 1;
      }
    }

    if ((i & AmbientOcclusion.A) == AmbientOcclusion.A) apply(data, preCalculated[0]);
    if ((i & AmbientOcclusion.B) == AmbientOcclusion.B) apply(data, preCalculated[1]);
    if ((i & AmbientOcclusion.C) == AmbientOcclusion.C) apply(data, preCalculated[2]);

    if ((i & AmbientOcclusion.D) == AmbientOcclusion.D) apply(data, preCalculated[3]);
    if ((i & AmbientOcclusion.E) == AmbientOcclusion.E) apply(data, preCalculated[4]);

    if ((i & AmbientOcclusion.F) == AmbientOcclusion.F) apply(data, preCalculated[5]);
    if ((i & AmbientOcclusion.G) == AmbientOcclusion.G) apply(data, preCalculated[6]);
    if ((i & AmbientOcclusion.H) == AmbientOcclusion.H) apply(data, preCalculated[7]);
  }

  private static void setupPixmap(Pixmap p, int i, Color c) {
    p.setColor(c);

    if ((i & AmbientOcclusion.A) == AmbientOcclusion.A) p.fillRectangle(0, 0, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE);
    if ((i & AmbientOcclusion.B) == AmbientOcclusion.B) p.fillRectangle(AmbientOcclusion.INDIVIDUAL_SIZE, 0, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE);
    if ((i & AmbientOcclusion.C) == AmbientOcclusion.C) p.fillRectangle(AmbientOcclusion.INDIVIDUAL_SIZE * 2, 0, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE);

    if ((i & AmbientOcclusion.D) == AmbientOcclusion.D) p.fillRectangle(0, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE);
    if ((i & AmbientOcclusion.E) == AmbientOcclusion.E) p.fillRectangle(AmbientOcclusion.INDIVIDUAL_SIZE * 2, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE);

    if ((i & AmbientOcclusion.F) == AmbientOcclusion.F) p.fillRectangle(0, AmbientOcclusion.INDIVIDUAL_SIZE * 2, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE);
    if ((i & AmbientOcclusion.G) == AmbientOcclusion.G) p.fillRectangle(AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE * 2, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE);
    if ((i & AmbientOcclusion.H) == AmbientOcclusion.H) p.fillRectangle(AmbientOcclusion.INDIVIDUAL_SIZE * 2, AmbientOcclusion.INDIVIDUAL_SIZE * 2, AmbientOcclusion.INDIVIDUAL_SIZE, AmbientOcclusion.INDIVIDUAL_SIZE);
  }

  private static double[][] gaussianWeightMatrix(double sigma) {
    return gaussianWeightMatrix((int) (sigma *2.625), sigma);
  }

  private static double[][] gaussianWeightMatrix(int radius, double sigma) {
    double[][] d = new double[1 + (2 * radius)][1 + (2 * radius)];
    double total = 0;
    for (int x = -radius; x <= radius; x++) {
      for (int y = -radius; y <= radius; y++) {
        double g = (1 / (2 * Math.PI * sigma * sigma)) * Math.pow(Math.E, -((x * x) + (y * y)) / (2 * sigma * sigma));
        d[x + radius][y + radius] = g;
        total += g;
      }
    }
    for (int i = 0; i < d.length; i++) {
      for (int j = 0; j < d[i].length; j++) {
        d[i][j] /= total;
      }
    }
    return d;
  }

  public static void main(final String[] args) {
    new HeadlessApplication(new ApplicationAdapter() {
      @Override
      public void create() {
        generate(Gdx.files.absolute(new File("ao-texture.png").getAbsolutePath()));
        System.exit(0);
      }
    });
  }
}

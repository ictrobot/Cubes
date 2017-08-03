package ethanjones.cubes.graphics.world.ao;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

import static ethanjones.cubes.graphics.world.ao.AmbientOcclusion.*;

public class AOTextureGenerator {

  public static final Pixmap.Format FORMAT = Pixmap.Format.RGBA8888;
  public static final Color BACKGROUND = Color.WHITE;
  public static final int SIGMA = 8;

  protected static void generate(FileHandle folder) {
    folder.mkdirs();
    for (Strength strength : Strength.values()) {
      generate(folder.child(strength.name() + ".png"), strength.strength);
    }
  }

  protected static void generate(FileHandle file, float strength) {
    Pixmap blocks = new Pixmap(INDIVIDUAL_SIZE * 3, INDIVIDUAL_SIZE * 3, AOTextureGenerator.FORMAT);
    Pixmap gaussian = new Pixmap(INDIVIDUAL_SIZE, INDIVIDUAL_SIZE, AOTextureGenerator.FORMAT);

    Pixmap output = new Pixmap(TEXTURE_SIZE, TEXTURE_SIZE, AOTextureGenerator.FORMAT);

    double[][] gwm = AOTextureGenerator.gaussianWeightMatrix(SIGMA);
    int gaussianRadius = (gwm.length - 1) / 2;

    Color blockColor = new Color(strength, strength, strength, 1f);

    for (int i = 0; i < TOTAL; i++) {
      String n = name(i);
      System.out.print(n + " ");

      AOTextureGenerator.clearPixmap(blocks);
      AOTextureGenerator.clearPixmap(gaussian);

      AOTextureGenerator.setupPixmap(blocks, i, blockColor);

      AOTextureGenerator.gaussianPixmap(blocks, gaussian, gwm, gaussianRadius);

      //PixmapIO.writePNG(folder.child(n + "_blocks_" + sigma + ".png"), blocks);
      //PixmapIO.writePNG(folder.child(n + "_gaussian_" + sigma + ".png"), gaussian);

      output.drawPixmap(gaussian, (i % SQRT_TOTAL) * INDIVIDUAL_SIZE, (i / SQRT_TOTAL) * INDIVIDUAL_SIZE, 0, 0, INDIVIDUAL_SIZE, INDIVIDUAL_SIZE);

      if (i % SQRT_TOTAL == SQRT_TOTAL - 1) System.out.println();
    }

    PixmapIO.writePNG(file, output);
    output.dispose();
    blocks.dispose();
    gaussian.dispose();
  }

  private static void gaussianPixmap(Pixmap in, Pixmap out, double[][] gwm, int gaussianRadius) {
    Color inColor = new Color();
    Color outColor = new Color();

    int offsetX = (in.getWidth() - out.getWidth()) / 2;
    int offsetY = (in.getHeight() - out.getHeight()) / 2;

    for (int x = 0; x < out.getWidth(); x++) {
      for (int y = 0; y < out.getHeight(); y++) {
        outColor.set(0, 0, 0, 0);

        for (int ox = -gaussianRadius; ox <= gaussianRadius; ox++) {
          for (int oy = -gaussianRadius; oy <= gaussianRadius; oy++) {
            int pixel = in.getPixel(x + ox + offsetX, y + oy + offsetY);
            inColor.set(pixel);
            double d = gwm[ox + gaussianRadius][oy + gaussianRadius];
            outColor.r += inColor.r * d;
            outColor.g += inColor.g * d;
            outColor.b += inColor.b * d;
            outColor.a += inColor.a * d;
          }
        }

        out.drawPixel(x, y, Color.rgba8888(outColor.clamp()));
      }
    }
  }

  private static void clearPixmap(Pixmap p) {
    p.setColor(BACKGROUND);
    p.fill();
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
    double[][] previous = null;
    for (int r = 1; r < sigma * 4; r++) {
      double[][] gwm = gaussianWeightMatrix(r, sigma);
      if (gwm[r][0] < 0.0001) {
        if (previous == null) return gwm;
        return previous;
      }
      previous = gwm;
    }
    return previous;
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
}

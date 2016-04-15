package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SmoothWorld extends TerrainGenerator {

  private static Random random = new Random();

  private final long baseSeed;
  private final Feature temperature;
  private final Feature rainfall;
  private final Feature height;
  private final Feature heightVariation;
  private final Feature trees;

  public SmoothWorld() {
    this(random.nextLong());
  }

  public SmoothWorld(long baseSeed) {
    this.baseSeed = baseSeed;
    temperature = new Feature(baseSeed + 1, 4, 1);
    rainfall = new Feature(baseSeed + 2, 4, 1);
    height = new Feature(baseSeed + 3, 4, 1);
    heightVariation = new Feature(baseSeed + 4, 4, 2);
    trees = new Feature(baseSeed + 5, 1, 3);
  }

  public static void main(String[] args) throws IOException {
    SmoothWorld smoothWorld = new SmoothWorld(0);
    BufferedImage image = new BufferedImage(4096, 4096, BufferedImage.TYPE_INT_RGB);
    for (int y = 0; y < 4096; y++) {
      for (int x = 0; x < 4096; x++) {
        double value = smoothWorld.height.eval(x, y);
        int c = (int) (255 * value);
        image.setRGB(x, y, new Color(c, c, c).getRGB());
      }
    }
    ImageIO.write(image, "png", new File("noise.png"));
    for (int y = 0; y < 4096; y++) {
      for (int x = 0; x < 4096; x++) {
        double value = smoothWorld.heightVariation.eval(x, y);
        int c = (int) (255 * value);
        image.setRGB(x, y, new Color(c, c, c).getRGB());
      }
    }
    ImageIO.write(image, "png", new File("noise2.png"));
  }

  @Override
  public void generate(Area area) {
    for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
      for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
        int g = getSurfaceHeight(area, x, z);
        set(area, Blocks.bedrock, x, 0, z);
        for (int y = 1; y < g; y++) {
          set(area, Blocks.stone, x, y, z);
        }
        set(area, Blocks.grass, x, g, z);
      }
    }
  }

  @Override
  public void features(Area area, WorldServer world) {
    Random r = getRandom("tree");
    for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
      for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
        double t = trees.eval(area.areaX + x, area.areaZ + z);
        if (t > 0.5d) {
          int trees = 100 - ((int) ((t - 0.4d) / 0.05d));
          if (r.nextInt(trees) == 0) {
            genTree(area, world, x, z, r);
          }
        }
      }
    }
  }

  @Override
  public BlockReference spawnPoint(WorldServer world) {
    return new BlockReference().setFromBlockCoordinates(0, getSurfaceHeight(0, 0) + 1, 0);
  }

  public void genTree(Area area, WorldServer world, int x, int z, Random r) {
    int y = getSurfaceHeight(area, x, z) + 1;
    int h = r.nextInt(4) + 2;

    x += area.minBlockX;
    z += area.minBlockZ;

    //set(world, Blocks.leaves, x - 2, y + h, z - 2);
    set(world, Blocks.leaves, x - 1, y + h, z - 2);
    set(world, Blocks.leaves, x + 0, y + h, z - 2);
    set(world, Blocks.leaves, x + 1, y + h, z - 2);
    //set(world, Blocks.leaves, x + 2, y + h, z - 2);

    set(world, Blocks.leaves, x - 2, y + h, z - 1);
    set(world, Blocks.leaves, x - 1, y + h, z - 1);
    set(world, Blocks.leaves, x + 0, y + h, z - 1);
    set(world, Blocks.leaves, x + 1, y + h, z - 1);
    set(world, Blocks.leaves, x + 2, y + h, z - 1);

    set(world, Blocks.leaves, x - 2, y + h, z + 0);
    set(world, Blocks.leaves, x - 1, y + h, z + 0);
    set(world, Blocks.leaves, x + 0, y + h, z + 0);
    set(world, Blocks.leaves, x + 1, y + h, z + 0);
    set(world, Blocks.leaves, x + 2, y + h, z + 0);

    set(world, Blocks.leaves, x - 2, y + h, z + 1);
    set(world, Blocks.leaves, x - 1, y + h, z + 1);
    set(world, Blocks.leaves, x + 0, y + h, z + 1);
    set(world, Blocks.leaves, x + 1, y + h, z + 1);
    set(world, Blocks.leaves, x + 2, y + h, z + 1);

    //set(world, Blocks.leaves, x - 2, y + h, z + 2);
    set(world, Blocks.leaves, x - 1, y + h, z + 2);
    set(world, Blocks.leaves, x + 0, y + h, z + 2);
    set(world, Blocks.leaves, x + 1, y + h, z + 2);
    //set(world, Blocks.leaves, x + 2, y + h, z + 2);

    //Second layer

    set(world, Blocks.leaves, x - 2, y + h + 1, z + 0);
    set(world, Blocks.leaves, x - 1, y + h + 1, z + 0);
    set(world, Blocks.leaves, x + 0, y + h + 1, z + 0);
    set(world, Blocks.leaves, x + 1, y + h + 1, z + 0);
    set(world, Blocks.leaves, x + 2, y + h + 1, z + 0);

    set(world, Blocks.leaves, x + 0, y + h + 1, z - 2);
    set(world, Blocks.leaves, x + 0, y + h + 1, z - 1);
    set(world, Blocks.leaves, x + 0, y + h + 1, z + 0);
    set(world, Blocks.leaves, x + 0, y + h + 1, z + 1);
    set(world, Blocks.leaves, x + 0, y + h + 1, z + 2);

    set(world, Blocks.leaves, x + 1, y + h + 1, z + 1);
    set(world, Blocks.leaves, x + 1, y + h + 1, z - 1);
    set(world, Blocks.leaves, x - 1, y + h + 1, z + 1);
    set(world, Blocks.leaves, x - 1, y + h + 1, z - 1);

    // Third layer

    set(world, Blocks.leaves, x + 0, y + h + 2, z + 0);
    set(world, Blocks.leaves, x + 0, y + h + 2, z + 1);
    set(world, Blocks.leaves, x + 0, y + h + 2, z - 1);
    set(world, Blocks.leaves, x + 1, y + h + 2, z + 0);
    set(world, Blocks.leaves, x - 1, y + h + 2, z + 0);

    for (int i = 0; i < h + 2; i++) {
      set(world, Blocks.log, x, y + i, z);
    }
  }

  public int getSurfaceHeight(Area area, int x, int z) {
    return getSurfaceHeight(area.minBlockX + x, area.minBlockZ + z);
  }

  public int getSurfaceHeight(int x, int z) {
    double h = height.eval(x, z) * 60;
    double hv = Math.sqrt(heightVariation.eval(x, z) + 1);
    return (int) Math.pow(h, hv);
  }

  private Random getRandom(String string) {
    return new Random((long) (baseSeed + string.hashCode()));
  }
}

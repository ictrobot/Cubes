package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

import java.util.Random;

public class SmoothWorld extends TerrainGenerator {
  public static final int minSurfaceHeight = 40;
  private static Random randomSeed = new Random();

  public final long baseSeed;
  private final Feature height;
  private final Feature heightVariation;
  private final Feature trees;
  private final CaveManager caves;

  public SmoothWorld() {
    this(randomSeed.nextLong());
  }

  public SmoothWorld(long baseSeed) {
    this.baseSeed = murmurHash3(baseSeed);
    Log.info("Smooth World Seed: " + baseSeed + " [" + this.baseSeed + "]");

    height = new Feature(murmurHash3(this.baseSeed + 1), 4, 1);
    heightVariation = new Feature(murmurHash3(this.baseSeed + 2), 4, 2);
    trees = new Feature(murmurHash3(this.baseSeed + 3), 1, 3);

    caves = new CaveManager(this);
  }

  @Override
  public void generate(Area area) {
    area.lock.writeLock();

    int blockBedrock = Sided.getIDManager().toInt(Blocks.bedrock);
    int blockStone = Sided.getIDManager().toInt(Blocks.stone);
    int blockDirt = Sided.getIDManager().toInt(Blocks.dirt);
    int blockGrass = Sided.getIDManager().toInt(Blocks.grass);

    for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
      for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
        int g = getSurfaceHeight(x + area.minBlockX, z + area.minBlockZ);
        int d = getDirtHeight(x + area.minBlockX, z + area.minBlockZ);

        if ((x == 0 && z == 0) || g > area.maxY) area.setupArrays(g);

        area.blocks[Area.getRef(x, 0, z)] = blockBedrock;
        for (int y = 1; y < g; y++) {
          if (y < (g - d))
            area.blocks[Area.getRef(x, y, z)] = blockStone;
          else
            area.blocks[Area.getRef(x, y, z)] = blockDirt;
        }
        area.blocks[Area.getRef(x, g, z)] = blockGrass;
      }
    }
    caves.apply(area);
    area.lock.writeUnlock();
  }

  @Override
  public void features(Area area, WorldServer world) {
    for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
      for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
        double t = trees.eval(area.areaX + x, area.areaZ + z);
        if (t > 0.5d) {
          int trees = 100 - ((int) ((t - 0.4d) / 0.05d));
          if (pseudorandomInt(x + area.minBlockX, z + area.minBlockZ, trees) == 0) {
            genTree(area, world, x, z);
          }
        }
      }
    }
  }

  @Override
  public BlockReference spawnPoint(WorldServer world) {
    return new BlockReference().setFromBlockCoordinates(0, getSurfaceHeight(0, 0) + 1, 0);
  }

  public void genTree(Area area, WorldServer world, int x, int z) {
    x += area.minBlockX;
    z += area.minBlockZ;

    int y = getSurfaceHeight(x, z) + 1;
    int h = getTreeHeight(x, z) + 1;

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

  public int getSurfaceHeight(int x, int z) {
    double h = height.eval(x, z) * 20;
    double hv = Math.sqrt(heightVariation.eval(x, z) + 1);
    return (int) Math.pow(minSurfaceHeight + h, hv);
  }

  public int getDirtHeight(int x, int z) {
    double h = height.eval(x, z) * 100;
    return 1 + ((int) Math.floor(h % 3));
  }

  public int getTreeHeight(int x, int z) {
    double h = heightVariation.eval(x, z) * 100;
    return 2 + ((int) Math.floor(h % 3));
  }

  public long pseudorandomBits(long x, long z, int bits, boolean murmurHash3) {
    long l = x + z + (x * (x - 1)) + (z * (z + 1)) + (long) Math.pow(x, z > 0 ? z : (z < 0 ? -z : 1));
    if (murmurHash3) l = murmurHash3(l);
    l += baseSeed;

    long multiplier = 0x5DEECE66DL;
    long addend = 0xBL;

    long l1 = l * multiplier + addend;
    long l2 = l1 * multiplier + addend;
    long lo = (l1 << 32) + l2;
    return lo >>> (64 - bits);
  }

  public int pseudorandomInt(long x, long z, int inclusiveBound) {
    float f = pseudorandomBits(x, z, 24, false) / ((float) (1 << 24));
    return (int) Math.floor(f * (inclusiveBound + 1));
  }

  public static long murmurHash3(long x) {
    x ^= x >>> 33;
    x *= 0xff51afd7ed558ccdL;
    x ^= x >>> 33;
    x *= 0xc4ceb9fe1a85ec53L;
    x ^= x >>> 33;

    return x;
  }
}

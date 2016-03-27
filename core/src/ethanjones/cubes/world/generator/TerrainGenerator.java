package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

public abstract class TerrainGenerator {

  public abstract void generate(Area area);

  public abstract void features(Area area, WorldServer world);

  public abstract BlockReference spawnPoint(WorldServer world);

  public static void set(Area area, Block block, int x, int y, int z) {
    int ref = Area.getRef(x, y, z);

    area.lock.writeLock();
    area.setupArrays(y);
    area.blocks[ref] = Sided.getBlockManager().toInt(block);
    area.lock.writeUnlock();
  }

  public static void set(WorldServer world, Block block, int x, int y, int z) {
    AreaReference areaReference = new AreaReference().setFromBlockCoordinates(x, z);
    world.lock.readLock();
    Area area = world.getArea(areaReference, false);
    if (area == null) {
      world.lock.readUnlock();
      throw new IllegalStateException(areaReference.toString());
    }
    world.lock.readUnlock();

    set(area, block, x - area.minBlockX, y, z - area.minBlockZ);
    //area.setBlock(block, x - area.minBlockX, y, z - area.minBlockZ);
  }
}

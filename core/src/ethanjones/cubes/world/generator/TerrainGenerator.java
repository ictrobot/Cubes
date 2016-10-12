package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.WorldSection;

public abstract class TerrainGenerator {

  public abstract void generate(Area area);

  public abstract void features(Area area, WorldServer world, WorldSection section);

  public abstract BlockReference spawnPoint(WorldServer world);

  public static void set(Area area, Block block, int x, int y, int z) {
    int ref = Area.getRef(x, y, z);

    area.lock.writeLock();
    area.setupArrays(y);
    area.blocks[ref] = Sided.getIDManager().toInt(block);
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
  }

  public static void set(WorldSection section, Block block, int x, int y, int z) {
    Area area = section.getAreaBlockCoordinates(x, z);
    set(area, block, x - area.minBlockX, y, z - area.minBlockZ);
  }

  public static void setVisible(Area area, Block block, int x, int y, int z) {
    int ref = Area.getRef(x, y, z);

    area.lock.writeLock();
    area.setupArrays(y);
    area.blocks[ref] = Sided.getIDManager().toInt(block) | Area.BLOCK_VISIBLE;
    area.lock.writeUnlock();
  }

  public static void setVisible(WorldServer world, Block block, int x, int y, int z) {
    AreaReference areaReference = new AreaReference().setFromBlockCoordinates(x, z);
    world.lock.readLock();
    Area area = world.getArea(areaReference, false);
    if (area == null) {
      world.lock.readUnlock();
      throw new IllegalStateException(areaReference.toString());
    }
    world.lock.readUnlock();

    setVisible(area, block, x - area.minBlockX, y, z - area.minBlockZ);
  }

  public static void setVisible(WorldSection section, Block block, int x, int y, int z) {
    Area area = section.getAreaBlockCoordinates(x, z);
    setVisible(area, block, x - area.minBlockX, y, z - area.minBlockZ);
  }
}

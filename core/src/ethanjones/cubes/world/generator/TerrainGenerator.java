package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

public abstract class TerrainGenerator {

  public abstract void generate(Area area);

  public abstract void features(Area area, WorldServer world);

  public abstract BlockReference spawnPoint(WorldServer world);

  public static void set(Area area, Block block, int x, int y, int z, int meta) {
    int ref = Area.getRef(x, y, z);

    area.lock.writeLock();
    area.setupArrays(y);
    area.blocks[ref] = (block == null ? 0 : block.intID + ((meta & 0xFF) << 20));
    area.lock.writeUnlock();
  }
  
  public static void setNeighbour(Area area, Block block, int x, int y, int z, int meta) {
    Area a = area.neighbourBlockCoordinates(x, z);
    set(a, block, x - a.minBlockX, y, z - a.minBlockZ, meta);
  }

  public static void setVisible(Area area, Block block, int x, int y, int z, int meta) {
    int ref = Area.getRef(x, y, z);

    area.lock.writeLock();
    area.setupArrays(y);
    area.blocks[ref] = (block == null ? 0 : (block.intID + ((meta & 0xFF) << 20)) | Area.BLOCK_VISIBLE);
    area.lock.writeUnlock();
  }

  public static void setVisibleNeighbour(Area area, Block block, int x, int y, int z, int meta) {
    Area a = area.neighbourBlockCoordinates(x, z);
    setVisible(a, block, x - a.minBlockX, y, z - a.minBlockZ, meta);
  }
}

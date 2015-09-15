package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.ThreadedWorld;

public abstract class TerrainGenerator {

  public abstract void generate(Area area);

  public abstract void features(Area area, WorldServer world);

  public abstract BlockReference spawnPoint(WorldServer world);

  protected void set(Area area, Block block, int x, int y, int z) {
    int ref = area.getRef(x, y, z);
    area.checkArrays();
    synchronized (area) {
      if (y > area.maxY) area.expand(y);
      area.blocks[ref] = Sided.getBlockManager().toInt(block);
    }
  }

  protected void set(WorldServer world, Block block, int x, int y, int z) {
    AreaReference areaReference = new AreaReference().setFromBlockCoordinates(x, z);
    Area area = world.getAreaInternal(areaReference, false);
    if (area == null) area = ThreadedWorld.generate(areaReference, world);
    area.setBlock(block, x - area.minBlockX, y, z - area.minBlockZ);
  }
}

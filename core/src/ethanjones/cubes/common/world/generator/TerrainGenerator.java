package ethanjones.cubes.common.world.generator;

import ethanjones.cubes.common.block.Block;
import ethanjones.cubes.common.Sided;
import ethanjones.cubes.common.world.storage.Area;

public abstract class TerrainGenerator {

  public abstract void generate(Area area);

  protected void set(Area area, Block block, int x, int y, int z) {
    int ref = area.getRef(x, y, z);
    area.checkArrays();
    synchronized (area) {
      area.blocks[ref] = Sided.getBlockManager().toInt(block);
    }
  }
}

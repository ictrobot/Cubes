package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.storage.Area;

public abstract class WorldGenerator {

  public abstract void generate(Area area);

  public void set(Area area, Block block, int x, int y, int z) {
    int ref = area.getRef(x, y, z);
    area.blockFactories[ref] = Sided.getBlockManager().toInt(block);
  }
}

package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.storage.Area;

public class BasicWorldGenerator extends WorldGenerator {

  @Override
  public void generate(Area area) {
    if (area.y == 0) {
      for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
        for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
          set(area, Blocks.bedrock, x, 0, z);
          set(area, Blocks.stone, x, 1, z);
          set(area, Blocks.dirt, x, 3, z);
          set(area, Blocks.grass, x, 4, z);
        }
      }
      if (area.x == 0 && area.z == 0) {
        set(area, Blocks.bedrock, 1, 4, 1);
        set(area, Blocks.bedrock, 1, 7, 1);
        set(area, Blocks.stone, 5, 5, 3);
        set(area, Blocks.grass, 3, 5, 5);
      }
    }
  }

  public void set(Area area, Block block, int x, int y, int z) {
    int ref = area.getRef(x, y, z);
    area.blockFactories[ref] = Sided.getBlockManager().toInt(block);
  }
}

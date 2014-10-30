package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.world.storage.Area;

public class BasicWorldGenerator extends WorldGenerator {

  @Override
  public void generate(Area area) {
    if (area.y == 0) {
      for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
        for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
          area.setBlock(Blocks.bedrock, x, 0, z, false);
          area.setBlock(Blocks.stone, x, 1, z, false);
          area.setBlock(Blocks.stone, x, 2, z, false);
          area.setBlock(Blocks.dirt, x, 3, z, false);
          area.setBlock(Blocks.grass, x, 4, z, false);
        }
      }
      if (area.x == 0 && area.z == 0) {
        area.setBlock(Blocks.bedrock, 1, 4, 1, false);
        area.setBlock(Blocks.bedrock, 1, 7, 1, false);
        area.setBlock(Blocks.stone, 5, 5, 3, false);
        area.setBlock(Blocks.grass, 3, 5, 5, false);
      }
    }
  }
}

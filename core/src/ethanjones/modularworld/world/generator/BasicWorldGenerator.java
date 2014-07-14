package ethanjones.modularworld.world.generator;

import ethanjones.modularworld.block.factory.BlockFactories;
import ethanjones.modularworld.world.storage.Area;

public class BasicWorldGenerator extends WorldGenerator {

  @Override
  public void generate(Area area) {
    if (area.y == 0) {
      for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
        for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
          area.setBlock(BlockFactories.bedrock.getBlock(), x, 0, z);
          area.setBlock(BlockFactories.stone.getBlock(), x, 1, z);
          area.setBlock(BlockFactories.stone.getBlock(), x, 2, z);
          area.setBlock(BlockFactories.dirt.getBlock(), x, 3, z);
          area.setBlock(BlockFactories.grass.getBlock(), x, 4, z);
        }
      }
      if (area.x == 0 && area.z == 0) {
        area.setBlock(BlockFactories.bedrock.getBlock(), 1, 4, 1);
        area.setBlock(BlockFactories.bedrock.getBlock(), 1, 7, 1);
        area.setBlock(BlockFactories.stone.getBlock(), 5, 5, 3);
        area.setBlock(BlockFactories.grass.getBlock(), 3, 5, 5);
      }
    }
  }
}

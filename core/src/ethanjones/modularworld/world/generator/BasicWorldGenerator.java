package ethanjones.modularworld.world.generator;

import ethanjones.modularworld.block.factory.BlockFactories;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.world.storage.Area;

public class BasicWorldGenerator extends WorldGenerator {

  @Override
  public void generate(Area area) {
    if (area.y == 0) {
      for (int x = 0; x < Area.S; x++) {
        for (int z = 0; z < Area.S; z++) {
          area.setBlock(BlockFactories.bedrock.getBlock(new ByteData()), x, 0, z);
          area.setBlock(BlockFactories.stone.getBlock(new ByteData()), x, 1, z);
          area.setBlock(BlockFactories.stone.getBlock(new ByteData()), x, 2, z);
          area.setBlock(BlockFactories.dirt.getBlock(new ByteData()), x, 3, z);
          area.setBlock(BlockFactories.grass.getBlock(new ByteData()), x, 4, z);
        }
      }
      if (area.x == 0 && area.z == 0) {
        area.setBlock(BlockFactories.bedrock.getBlock(new ByteData()), 0, 4, 0);
        area.setBlock(BlockFactories.bedrock.getBlock(new ByteData()), 0, 7, 0);
        area.setBlock(BlockFactories.stone.getBlock(new ByteData()), 2, 5, 0);
        area.setBlock(BlockFactories.grass.getBlock(new ByteData()), 0, 5, 2);
      }
    }
  }
}

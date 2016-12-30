package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

public class BasicTerrainGenerator extends TerrainGenerator {

  @Override
  public void generate(Area area) {
    for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
      for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
        set(area, Blocks.bedrock, x, 0, z, 0);
        set(area, Blocks.stone, x, 1, z, 0);
        set(area, Blocks.stone, x, 2, z, 0);
        set(area, Blocks.dirt, x, 3, z, 0);
        set(area, Blocks.grass, x, 4, z, 0);
      }
    }
    if (area.areaX == 0 && area.areaZ == 0) {
      set(area, Blocks.bedrock, 1, 4, 1, 0);
      set(area, Blocks.bedrock, 1, 7, 1, 0);
      set(area, Blocks.stone, 5, 5, 3, 0);
      set(area, Blocks.grass, 3, 5, 5, 0);
    }
  }

  @Override
  public void features(Area area, WorldServer world) {

  }

  @Override
  public BlockReference spawnPoint(WorldServer world) {
    return new BlockReference().setFromBlockCoordinates(0, 5, 0);
  }
}

package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

public class TestTerrainGenerator extends TerrainGenerator {
  @Override
  public void generate(Area area) {
    if (area.areaX != 0 || area.areaZ != 0) return;

    for (int x = 0; x < Area.SIZE_BLOCKS; x++) {
      for (int y = 0; y < Area.SIZE_BLOCKS; y++) {
        for (int z = 0; z < Area.SIZE_BLOCKS; z++) {
          if (y % 2 == 0) {
            if (x % 2 == 0) {
              if (z % 2 == 0) set(area, Blocks.grass, x, y, z);
            } else {
              if (z % 2 == 1) set(area, Blocks.grass, x, y, z);
            }
          } else {
            if (x % 2 == 1) {
              if (z % 2 == 0) set(area, Blocks.grass, x, y, z);
            } else {
              if (z % 2 == 1) set(area, Blocks.grass, x, y, z);
            }
          }
        }
      }
    }
  }

  @Override
  public void features(Area area, WorldServer world) {

  }

  @Override
  public BlockReference spawnPoint(WorldServer world) {
    return new BlockReference().setFromBlockCoordinates(0, 32, 0);
  }
}

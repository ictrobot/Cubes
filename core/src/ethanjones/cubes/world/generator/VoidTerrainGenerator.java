package ethanjones.cubes.world.generator;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.world.generator.smooth.TreeGenerator;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.storage.Area;

public class VoidTerrainGenerator extends TerrainGenerator {
  @Override
  public void generate(Area area) {
    if (area.areaX != 0 || area.areaZ != 0) return;
  
    for (int x = 0; x < 5; x++) {
      for (int z = 0; z < 5; z++) {
        for (int y = 96; y < 100; y++) {
          set(area, Blocks.dirt, x, y, z, 0);
        }
        set(area, Blocks.grass, x, 100, z, 0);
      }
    }
  }
  
  @Override
  public void features(Area area, WorldServer world) {
    if (area.areaX == 0 && area.areaZ == 0) new TreeGenerator().generateTree(2, 101, 2, 3, area);
  }
  
  @Override
  public BlockReference spawnPoint(WorldServer world) {
    return new BlockReference().setFromBlockCoordinates(0, 101, 0);
  }
}

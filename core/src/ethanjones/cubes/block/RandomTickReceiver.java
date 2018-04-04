package ethanjones.cubes.block;

import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

public interface RandomTickReceiver {

  /**
   * coordinates are inside the area
   */
  void randomTick(World world, Area area, int x, int y, int z, int meta);

}

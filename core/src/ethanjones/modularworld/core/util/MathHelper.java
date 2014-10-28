package ethanjones.modularworld.core.util;

import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.Zone;

public class MathHelper {

  private static float area_size = Area.SIZE_BLOCKS;
  private static float zone_size = Zone.SIZE_AREAS;

  public static int block(float position) {
    return (int) Math.floor(position);
  }

  public static int area(float position) { //position or block
    return (int) Math.floor(position / area_size);
  }

  public static int zone(int area) {
    return (int) Math.floor(area / zone_size);
  }

}

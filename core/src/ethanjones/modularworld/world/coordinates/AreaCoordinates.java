package ethanjones.modularworld.world.coordinates;

import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.world.storage.Zone;

public class AreaCoordinates extends ZoneCoordinates {

  public final int areaX;
  public final int areaY;
  public final int areaZ;

  public AreaCoordinates(int areaX, int areaY, int areaZ) {
    super(zone(areaX), zone(areaZ));
    if (areaY < 0) {
      Log.error(new ModularWorldException("Y must be positive"));
    }
    this.areaX = areaX;
    this.areaY = areaY;
    this.areaZ = areaZ;
  }

  public static int zone(int area) {
    if (area >= 0) {
      return area / Zone.SIZE_AREAS;
    } else {
      return (area - Zone.SIZE_AREAS) / Zone.SIZE_AREAS;
    }
  }

}

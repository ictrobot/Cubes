package ethanjones.modularworld.world.storage;

import static ethanjones.modularworld.core.util.Maths.fastPositive;

public class Zone {

  public static final int SIZE_AREAS = 16;
  public static final int SIZE_BLOCKS = Zone.SIZE_AREAS * Area.SIZE_BLOCKS;
  private Area[][][] areas = new Area[SIZE_AREAS][SIZE_AREAS][SIZE_AREAS];
  public final int x;
  public final int z;
  public final int maxBlockX;
  public final int maxBlockZ;
  public final int minBlockX;
  public final int minBlockZ;

  /**
   * In zone coords
   */

  public Zone(int x, int z) {
    this.x = x;
    this.z = z;

    maxBlockX = ((x + 1) * SIZE_AREAS) - 1;
    maxBlockZ = ((z + 1) * SIZE_AREAS) - 1;
    minBlockX = x * SIZE_AREAS;
    minBlockZ = z * SIZE_AREAS;
  }

  /**
   * Area coords
   */
  public Area getArea(int x, int y, int z) {
    return areas[fastPositive(x % Zone.SIZE_AREAS)][fastPositive(y % Zone.SIZE_AREAS)][fastPositive(z % Zone.SIZE_AREAS)];
  }

  /**
   * Area coords
   */
  public void setArea(Area area) {
    areas[fastPositive(area.x % Zone.SIZE_AREAS)][fastPositive(area.y % Zone.SIZE_AREAS)][fastPositive(area.z % Zone.SIZE_AREAS)] = area;
  }
}

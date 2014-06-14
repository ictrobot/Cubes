package ethanjones.modularworld.world.storage;

public class Zone {
  
  public static final int S = 16;
  public static final int TotalS = Zone.S * Area.S;
  
  public final int x;
  public final int z;
  
  public final int maxBlockX;
  public final int maxBlockZ;
  public final int minBlockX;
  public final int minBlockZ;
  
  private Area[][][] areas = new Area[S][S][S];
  
  /**
   * In zone coords
   */
  
  public Zone(int x, int z) {
    this.x = x;
    this.z = z;
    
    maxBlockX = ((x + 1) * S) - 1;
    maxBlockZ = ((z + 1) * S) - 1;
    minBlockX = x * S;
    minBlockZ = z * S;
  }
  
  /**
   * Area coords
   */
  public Area getArea(int x, int y, int z) {
    return areas[Math.abs(x % Zone.S)][Math.abs(y % Zone.S)][Math.abs(z % Zone.S)];
  }
  
  /**
   * Area coords
   */
  public void setArea(Area area) {
    areas[Math.abs(area.x % Zone.S)][Math.abs(area.y % Zone.S)][Math.abs(area.z % Zone.S)] = area;
  }
}

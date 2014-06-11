package ethanjones.modularworld.world.storage;

public class Zone {
  
  public static final int S = 16;
  public static final int TotalS = Zone.S * Area.S;
  
  public final int x;
  public final int z;
  
  public final int maxNormalX;
  public final int maxNormalZ;
  public final int minNormalX;
  public final int minNormalZ;
  
  private Area[][][] areas = new Area[S][S][S];
  
  /**
   * In zone coords
   */
  public Zone(int x, int z) {
    this.x = x;
    this.z = z;
    
    maxNormalX = ((x + 1) * S) - 1;
    maxNormalZ = ((z + 1) * S) - 1;
    minNormalX = x * S;
    minNormalZ = z * S;
  }
  
  /**
   * Normal coords
   */
  public Area getArea(int x, int y, int z) {
    return areas[Math.abs((x % TotalS) / Area.S)][Math.abs((y % TotalS) / Area.S)][Math.abs((z % TotalS) / Area.S)];
  }
  
  /**
   * Normal coords
   */
  public void setArea(Area area, int x, int y, int z) {
    areas[Math.abs((x % TotalS) / Area.S)][Math.abs((y % TotalS) / Area.S)][Math.abs((z % TotalS) / Area.S)] = area;
  }
}

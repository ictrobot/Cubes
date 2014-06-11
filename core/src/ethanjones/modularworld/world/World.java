package ethanjones.modularworld.world;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.Zone;

public class World {
  
  public static int WORLD_RADIUS_ZONES = 1000;
  
  public Zone[][] zones = new Zone[WORLD_RADIUS_ZONES][WORLD_RADIUS_ZONES];
  public final WorldGenerator gen;
  
  public World(WorldGenerator gen) {
    this.gen = gen;
  }
  
  public Zone getZone(int x, int y, int z) {
    int zoneX = x / Zone.TotalS;
    int zoneZ = z / Zone.TotalS;
    if (zones[zoneX][zoneZ] == null) {
      zones[zoneX][zoneZ] = new Zone(zoneX, zoneZ);;
    }
    return zones[zoneX][zoneZ];
  }
  
  public Area getArea(int x, int y, int z) {
    Zone zone = getZone(x, y, z);
    int areaX = x / Area.S;
    int areaY = y / Area.S;
    int areaZ = z / Area.S;
    if (zone.getArea(x, y, z) == null) {
      zone.setArea(new Area(areaX, areaY, areaZ), x, y, z);
    }
    Area area = zone.getArea(x, y, z);
    if (!area.generated) {
      gen.generate(area);
      area.generated = true;
    }
    return area;
  }
  
  public Block getBlock(int x, int y, int z) {
    return getArea(x, y, z).getBlock(x, y, z);
  }
  
}

package ethanjones.modularworld.world;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.Zone;

public class World {
  
  public final static int WORLD_RADIUS_ZONES = 1000;
  public final static int HEIGHT_LIMIT = Zone.TotalS;
  
  public Zone[][] zones = new Zone[WORLD_RADIUS_ZONES * 2][WORLD_RADIUS_ZONES * 2];
  public final WorldGenerator gen;
  
  public World(WorldGenerator gen) {
    this.gen = gen;
  }
  
  public Zone getZone(int x, int y, int z) {
    int zoneX;
    int zoneZ;
    if (x >= 0) {
      zoneX = x / Zone.TotalS;
    } else {
      zoneX = (x - Zone.TotalS) / Zone.TotalS;
    }
    if (z >= 0) {
      zoneZ = z / Zone.TotalS;
    } else {
      zoneZ = (z - Zone.TotalS) / Zone.TotalS;
    }
    if (zones[WORLD_RADIUS_ZONES + zoneX][WORLD_RADIUS_ZONES + zoneZ] == null) {
      zones[WORLD_RADIUS_ZONES + zoneX][WORLD_RADIUS_ZONES + zoneZ] = new Zone(zoneX, zoneZ);;
    }
    return zones[WORLD_RADIUS_ZONES + zoneX][WORLD_RADIUS_ZONES + zoneZ];
  }
  
  public Area getArea(int x, int y, int z) {
    Zone zone = getZone(x, y, z);
    int areaX;
    int areaY;
    int areaZ;
    if (x >= 0) {
      areaX = x / Area.S;
    } else {
      areaX = (x - Area.S) / Area.S;
    }
    if (y >= 0) {
      areaY = y / Area.S;
    } else {
      throw new RuntimeException("Y must be positive");
    }
    if (z >= 0) {
      areaZ = z / Area.S;
    } else {
      areaZ = (z - Area.S) / Area.S;
    }
    
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

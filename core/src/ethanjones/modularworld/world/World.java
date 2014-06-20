package ethanjones.modularworld.world;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.block.rendering.BlockLocationCache;
import ethanjones.modularworld.core.events.world.generation.GenerationEvent;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.coordinates.ZoneCoordinates;
import ethanjones.modularworld.world.generator.WorldGenerator;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.Zone;

public class World {

  public final static int WORLD_RADIUS_ZONES = 1000;
  public final static int HEIGHT_LIMIT = Zone.TotalS;

  public Zone[][] zones = new Zone[WORLD_RADIUS_ZONES * 2][WORLD_RADIUS_ZONES * 2];
  public final WorldGenerator gen;
  public final BlockLocationCache blockLocationCache;

  public World(WorldGenerator gen) {
    this.gen = gen;
    blockLocationCache = new BlockLocationCache();
  }

  public Zone getZone(int x, int y, int z) {
    return getZone(new BlockCoordinates(x, y, z));
  }

  public Zone getZone(ZoneCoordinates coords) {
    int dimX = WORLD_RADIUS_ZONES + coords.zoneX;
    int dimZ = WORLD_RADIUS_ZONES + coords.zoneZ;
    if (zones[dimX][dimZ] == null) {
      zones[dimX][dimZ] = new Zone(coords.zoneX, coords.zoneZ);
    }
    return zones[dimX][dimZ];
  }

  public Area getArea(int x, int y, int z) {
    return getArea(new BlockCoordinates(x, y, z));
  }

  public Area getArea(AreaCoordinates coords) {
    Zone zone = getZone(coords);

    Area area = zone.getArea(coords.areaX, coords.areaY, coords.areaZ);
    if (area == null) {
      area = new Area(coords.areaX, coords.areaY, coords.areaZ);
      zone.setArea(area);
    }
    if (!area.generated) {
      gen.generate(area);
      new GenerationEvent(area, coords).post();
      area.generated = true;
    }
    return area;
  }

  public Block getBlock(int x, int y, int z) {
    return getArea(x, y, z).getBlock(x, y, z);
  }

  public void dispose() {
    blockLocationCache.dispose();
  }

}

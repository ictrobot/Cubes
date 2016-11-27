package ethanjones.cubes.world.thread;

import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.AreaMap;

//TODO remove?
public class WorldSection {
  public final int initialAreaX;
  public final int initialAreaZ;
  public final int initialMinBlockX;
  public final int initialMinBlockZ;
  public final int initialMaxBlockX;
  public final int initialMaxBlockZ;
  public final Area[][] areas = new Area[3][3];
  public final Area initial;

  public WorldSection(Area initial) {
    initialAreaX = initial.areaX;
    initialAreaZ = initial.areaZ;
    initialMinBlockX = initial.minBlockX;
    initialMinBlockZ = initial.minBlockZ;
    initialMaxBlockX = initial.minBlockX + Area.SIZE_BLOCKS;
    initialMaxBlockZ = initial.minBlockZ + Area.SIZE_BLOCKS;
    this.initial = initial;
  
    AreaMap map = initial.areaMap();
    areas[0][0] = map.getArea(initialAreaX - 1, initialAreaZ - 1);
    areas[0][1] = map.getArea(initialAreaX - 1, initialAreaZ);
    areas[0][2] = map.getArea(initialAreaX - 1, initialAreaZ + 1);
    areas[1][0] = map.getArea(initialAreaX, initialAreaZ - 1);
    areas[1][1] = initial;
    areas[1][2] = map.getArea(initialAreaX, initialAreaZ + 1);
    areas[2][0] = map.getArea(initialAreaX + 1, initialAreaZ - 1);
    areas[2][1] = map.getArea(initialAreaX + 1, initialAreaZ);
    areas[2][2] = map.getArea(initialAreaX + 1, initialAreaZ + 1);

    for (Area[] areaArr : areas) {
      for (Area area : areaArr) {
        if (area == null) throw new AreaNotLoadedException();
      }
    }
  }

  public WorldSection(WorldSection section) {
    initialAreaX = section.initialAreaX;
    initialAreaZ = section.initialAreaZ;
    initialMinBlockX = section.initialMinBlockX;
    initialMinBlockZ = section.initialMinBlockZ;
    initialMaxBlockX = section.initialMaxBlockX;
    initialMaxBlockZ = section.initialMaxBlockZ;
    initial = section.initial;
    for (int i = 0; i < areas.length; i++) {
      for (int j = 0; j < areas[i].length; j++) {
        areas[i][j] = section.areas[i][j];
        if (areas[i][j] == null) throw new AreaNotLoadedException();
      }
    }
  }

  public Area getAreaBlockCoordinates(int x, int z) {
    if (x >= initialMinBlockX && x < initialMaxBlockX && z >= initialMinBlockZ && z < initialMaxBlockZ) return initial;
    int dX = CoordinateConverter.area(x) - initialAreaX;
    int dZ = CoordinateConverter.area(z) - initialAreaZ;
    return areas[dX + 1][dZ + 1];
  }

  public Area getArea(int areaX, int areaZ) {
    int dX = areaX - initialAreaX;
    int dZ = areaZ - initialAreaZ;
    return areas[dX + 1][dZ + 1];
  }
}
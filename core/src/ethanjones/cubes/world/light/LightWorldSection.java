package ethanjones.cubes.world.light;

import ethanjones.cubes.core.IDManager.TransparencyManager;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.AreaNotLoadedException;

class LightWorldSection {
  public final int initialAreaX;
  public final int initialAreaZ;
  public final Area[][] areas = new Area[3][3];
  private final TransparencyManager transparency;

  LightWorldSection(Area initial) {
    this.transparency = Sided.getIDManager().transparencyManager;
    initialAreaX = initial.areaX;
    initialAreaZ = initial.areaZ;

    World world = initial.world;
    areas[0][0] = world.getArea(initialAreaX - 1, initialAreaZ - 1);
    areas[0][1] = world.getArea(initialAreaX - 1, initialAreaZ);
    areas[0][2] = world.getArea(initialAreaX - 1, initialAreaZ + 1);
    areas[1][0] = world.getArea(initialAreaX, initialAreaZ - 1);
    areas[1][1] = initial;
    areas[1][2] = world.getArea(initialAreaX, initialAreaZ + 1);
    areas[2][0] = world.getArea(initialAreaX + 1, initialAreaZ - 1);
    areas[2][1] = world.getArea(initialAreaX + 1, initialAreaZ);
    areas[2][2] = world.getArea(initialAreaX + 1, initialAreaZ + 1);

    for (Area[] areaArr : areas) {
      for (Area area : areaArr) {
        if (area == null) throw new AreaNotLoadedException();
      }
    }
    Lock.waitToLockAll(true, areas[0][0], areas[0][1], areas[0][2], areas[1][0], areas[1][1], areas[1][2], areas[2][0], areas[2][1], areas[2][2]);
  }

  protected boolean transparent(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (!a.isReady()) return true;
    int ref = Area.getRef(x - a.minBlockX, y, z - a.minBlockZ);
    return transparency.isTransparent(a.blocks[ref]);
  }

  protected boolean transparent(Area a, int ref) {
    // simply using this class's reference to transparency for speed
    if (!a.isReady()) return true;
    return transparency.isTransparent(a.blocks[ref]);
  }

  protected int getSunlight(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (!a.isReady()) return 15;
    return (a.light[Area.getRef(x - a.minBlockX, y, z - a.minBlockZ)] >> 4) & 0xF;
  }

  protected int getLight(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (!a.isReady()) return 0;
    return a.light[Area.getRef(x - a.minBlockX, y, z - a.minBlockZ)] & 0xF;
  }

  protected boolean isLightSource(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (!a.isReady()) return false;
    int b = a.blocks[Area.getRef(x - a.minBlockX, y, z - a.minBlockZ)];
    if (b == 0) return false;
    int blockID = b & 0xFFFFF;
    return Sided.getIDManager().toBlock(blockID).getLightLevel() > 0;
  }

  protected int maxY(int x, int z) {
    return getArea(CoordinateConverter.area(x), CoordinateConverter.area(z)).maxY;
  }

  protected Area getArea(int areaX, int areaZ) {
    int dX = areaX - initialAreaX;
    int dZ = areaZ - initialAreaZ;
    return areas[dX + 1][dZ + 1];
  }

  protected void unlock() {
    for (Area[] areaArr : areas) {
      for (Area area : areaArr) {
        area.lock.writeUnlock();
      }
    }
  }
}

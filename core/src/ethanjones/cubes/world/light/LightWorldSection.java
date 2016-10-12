package ethanjones.cubes.world.light;

import ethanjones.cubes.core.IDManager.TransparencyManager;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.WorldSection;

class LightWorldSection extends WorldSection {
  private final TransparencyManager transparency;

  LightWorldSection(Area initial) {
    super(initial);
    this.transparency = Sided.getIDManager().transparencyManager;
    Lock.waitToLockAll(true, areas[0][0], areas[0][1], areas[0][2], areas[1][0], areas[1][1], areas[1][2], areas[2][0], areas[2][1], areas[2][2]);
  }

  LightWorldSection(WorldSection section) {
    super(section);
    this.transparency = Sided.getIDManager().transparencyManager;
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
    int blockMeta = (b >> 20) & 0xFF;
    return Sided.getIDManager().toBlock(blockID).getLightLevel(blockMeta) > 0;
  }

  protected int maxY(int x, int z) {
    return getArea(CoordinateConverter.area(x), CoordinateConverter.area(z)).maxY;
  }

  protected void unlock() {
    for (Area[] areaArr : areas) {
      for (Area area : areaArr) {
        area.lock.writeUnlock();
      }
    }
  }
}

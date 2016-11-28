package ethanjones.cubes.world.light;

import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.id.TransparencyManager;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.WorldSection;

class LightWorldSection extends WorldSection {

  LightWorldSection(Area initial) {
    super(initial);
    Lock.waitToLockAll(true, areas[0][0], areas[0][1], areas[0][2], areas[1][0], areas[1][1], areas[1][2], areas[2][0], areas[2][1], areas[2][2]);
  }

  LightWorldSection(WorldSection section) {
    super(section);
    Lock.waitToLockAll(true, areas[0][0], areas[0][1], areas[0][2], areas[1][0], areas[1][1], areas[1][2], areas[2][0], areas[2][1], areas[2][2]);
  }

  protected boolean transparent(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (!a.isReady()) return true;
    int ref = Area.getRef(x - a.minBlockX, y, z - a.minBlockZ);
    return TransparencyManager.isTransparent(a.blocks[ref]);
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
    return IDManager.toBlock(blockID).getLightLevel(blockMeta) > 0;
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

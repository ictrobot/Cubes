package ethanjones.cubes.world.light;

import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.id.TransparencyManager;
import ethanjones.cubes.core.util.locks.LockManager;
import ethanjones.cubes.core.util.locks.Locked;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.AreaNotLoadedException;

class LightWorldSection implements AutoCloseable {
  public final int initialAreaX;
  public final int initialAreaZ;
  public final int initialMinBlockX;
  public final int initialMinBlockZ;
  public final int initialMaxBlockX;
  public final int initialMaxBlockZ;
  public final Area[] areas = new Area[9];
  public final Area initial;
  private Locked<Area> lock;

  public LightWorldSection(Area initial) {
    initialAreaX = initial.areaX;
    initialAreaZ = initial.areaZ;
    initialMinBlockX = initial.minBlockX;
    initialMinBlockZ = initial.minBlockZ;
    initialMaxBlockX = initial.minBlockX + Area.SIZE_BLOCKS;
    initialMaxBlockZ = initial.minBlockZ + Area.SIZE_BLOCKS;
    this.initial = initial;
    
    areas[0] = initial.neighbour(initialAreaX - 1, initialAreaZ - 1);
    areas[1] = initial.neighbour(initialAreaX - 1, initialAreaZ);
    areas[2] = initial.neighbour(initialAreaX - 1, initialAreaZ + 1);
    areas[3] = initial.neighbour(initialAreaX, initialAreaZ - 1);
    areas[4] = initial;
    areas[5] = initial.neighbour(initialAreaX, initialAreaZ + 1);
    areas[6] = initial.neighbour(initialAreaX + 1, initialAreaZ - 1);
    areas[7] = initial.neighbour(initialAreaX + 1, initialAreaZ);
    areas[8] = initial.neighbour(initialAreaX + 1, initialAreaZ + 1);
    
    for (Area area : areas) {
      if (area == null) throw new AreaNotLoadedException();
    }

    lock = LockManager.lockMany(true, areas);
  }
  
  public Area getArea(int areaX, int areaZ) {
    int dX = areaX - initialAreaX + 1;
    int dZ = areaZ - initialAreaZ + 1;
    return areas[dX * 3 + dZ];
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

  @Override
  public void close() {
    lock.close();
  }
}

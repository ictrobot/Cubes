package ethanjones.cubes.world.light;

import ethanjones.cubes.core.IDManager.TransparencyManager;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

class LightWorldSection {
  public final int initialAreaX;
  public final int initialAreaZ;
  public final Area[][] areas = new Area[3][3];
  private final World world;
  private final TransparencyManager transparency;
  private final int ySection;

  LightWorldSection(Area initial, int ySection) {
    this.world = initial.world;
    this.transparency = Sided.getIDManager().transparencyManager;
    this.ySection = ySection;
    initialAreaX = initial.areaX;
    initialAreaZ = initial.areaZ;

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
        area.lock.writeLock();
      }
    }
  }

  protected boolean transparent(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    int ref = Area.getRef(x - a.minBlockX, y, z - a.minBlockZ);
    return transparency.isTransparent(a.blocks[ref]);
  }

  protected boolean transparent(Area a, int ref) {
    // simply using this class's reference to transparency for speed
    return transparency.isTransparent(a.blocks[ref]);
  }

  protected int getSunlight(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return (a.light[Area.getRef(x - a.minBlockX, y, z - a.minBlockZ)] >> 4) & 0xF;
  }

  protected int getLight(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return a.light[Area.getRef(x - a.minBlockX, y, z - a.minBlockZ)] & 0xF;
  }

  protected boolean isLightSource(int x, int y, int z) {
    Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    int b = a.blocks[Area.getRef(x, y, z)];
    if (b == 0) return false;
    if (b < 0) return Sided.getIDManager().toBlock(-b).getLightLevel() > 0;
    return Sided.getIDManager().toBlock(b).getLightLevel() > 0;
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
    boolean isClient = Sided.getSide() == Side.Client;
    for (Area[] areaArr : areas) {
      for (Area area : areaArr) {
        if (ySection != -1) {
          if (isClient && ySection - 1 < area.height && ySection != 0) area.updateRender(ySection - 1);
          if (isClient && ySection < area.height) area.updateRender(ySection);
          if (isClient && ySection + 1 < area.height) area.updateRender(ySection + 1);
        }

        area.lock.writeUnlock();
      }
    }
  }
}

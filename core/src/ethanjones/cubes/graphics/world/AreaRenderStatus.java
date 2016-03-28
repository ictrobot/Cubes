package ethanjones.cubes.graphics.world;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.world.storage.Area;

import static ethanjones.cubes.world.storage.Area.SIZE_BLOCKS;
import static ethanjones.cubes.world.storage.Area.SIZE_BLOCKS_CUBED;

public class AreaRenderStatus {

  public static int UNKNOWN = -2;
  public static int EMPTY = -1;
  public static int COMPLETE_MAX_X = 1;
  public static int COMPLETE_MIN_X = 2;
  public static int COMPLETE_MAX_Y = 4;
  public static int COMPLETE_MIN_Y = 8;
  public static int COMPLETE_MAX_Z = 16;
  public static int COMPLETE_MIN_Z = 32;
  public static int COMPLETE = 63;

  public static int update(Area area, int ySection) {
    if (area.renderStatus[ySection] != UNKNOWN) return area.renderStatus[ySection];
    area.lock.writeLock();
    int h = ySection * SIZE_BLOCKS;
    int m = SIZE_BLOCKS - 1;

    boolean b1 = checkComplete(area, h, 0, 0, 0, m, 0, 0);
    boolean b2 = checkComplete(area, h, 0, 0, 0, 0, 0, m);
    boolean b3 = checkComplete(area, h, m, 0, 0, m, 0, m);
    boolean b4 = checkComplete(area, h, 0, 0, m, m, 0, m);

    boolean t1 = checkComplete(area, h, 0, m, 0, m, m, 0);
    boolean t2 = checkComplete(area, h, 0, m, 0, 0, m, m);
    boolean t3 = checkComplete(area, h, m, m, 0, m, m, m);
    boolean t4 = checkComplete(area, h, 0, m, m, m, m, m);

    boolean s1 = checkComplete(area, h, 0, 0, 0, 0, m, 0);
    boolean s2 = checkComplete(area, h, m, 0, 0, m, m, 0);
    boolean s3 = checkComplete(area, h, 0, 0, m, 0, m, m);
    boolean s4 = checkComplete(area, h, m, 0, m, m, m, m);

    int a = 1;
    int b = SIZE_BLOCKS - 2;
    boolean fb = checkComplete(area, h, a, 0, a, b, 0, b);
    boolean ft = checkComplete(area, h, a, m, a, b, m, b);
    boolean fmaxx = checkComplete(area, h, m, a, a, m, b, b);
    boolean fminx = checkComplete(area, h, 0, a, a, 0, b, b);
    boolean fmaxz = checkComplete(area, h, a, a, m, b, b, m);
    boolean fminz = checkComplete(area, h, a, a, 0, b, b, 0);

    int status = 0;
    if (b2 && t2 && s1 && s3 && fminx) status += COMPLETE_MIN_X;
    if (b3 && t3 && s2 && s4 && fmaxx) status += COMPLETE_MAX_X;
    if (b1 && b2 && b3 && b4 && fb) status += COMPLETE_MIN_Y;
    if (t1 && t2 && t3 && t4 && ft) status += COMPLETE_MAX_Y;
    if (b1 && t1 && s1 && s2 && fminz) status += COMPLETE_MIN_Z;
    if (b4 && t4 && s3 && s4 && fmaxz) status += COMPLETE_MAX_Z;
    if (status == 0 && !(b1 || b2 || b3 || b4 || t1 || t2 || t3 || t4 || s1 || s2 || s3 || s4 || fb || ft || fmaxx || fminx || fmaxz || fminz)) {
      //just because the outside isn't complete doesn't mean there are no blocks
      if (checkEmpty(area, ySection)) status = EMPTY;
    }

    area.renderStatus[ySection] = status;
    area.lock.writeUnlock();
    return status;
  }

  // area should be locked
  private static boolean checkComplete(Area area, int h, int x1, int y1, int z1, int x2, int y2, int z2) {
    for (int x = x1; x <= x2; x++) {
      for (int y = y1 + h; y <= y2 + h; y++) {
        for (int z = z1; z <= z2; z++) {
          if (area.blocks[Area.getRef(x, y, z)] == 0) return false;
        }
      }
    }
    return true;
  }

  private static boolean checkEmpty(Area area, int ySection) {
    for (int i = ySection * SIZE_BLOCKS_CUBED; i < ((ySection + 1) * SIZE_BLOCKS_CUBED); i++) {
      if (area.blocks[i] != 0) return false;
    }
    return true;
  }

  public static int[] create(int ySections) {
    int[] ints = new int[ySections];
    for (int i = 0; i < ySections; i++) {
      ints[i] = UNKNOWN;
    }
    return ints;
  }
}

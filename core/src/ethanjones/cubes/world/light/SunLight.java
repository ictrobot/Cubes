package ethanjones.cubes.world.light;

import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;

import java.util.ArrayDeque;
import java.util.concurrent.locks.ReentrantLock;

import static ethanjones.cubes.world.storage.Area.*;
import static ethanjones.cubes.world.light.BlockLight.transparent;

public class SunLight {
  public static ReentrantLock initalSunlight = new ReentrantLock();
  public static final int MAX_SUNLIGHT = 0xF0;

  public static void initialSunlight(Area area) {
    initalSunlight.lock(); // used to prevent all the World Generation threads grabbing different areas and deadlocking
    LightWorldSection worldSection = new LightWorldSection(area, -1);
    initalSunlight.unlock();

    ArrayDeque<LightNode> lightQueue = new ArrayDeque<>();
    int max = 15;
    for (int x = 0; x < SIZE_BLOCKS; x++) {
      for (int z = 0; z < SIZE_BLOCKS; z++) {
        int hmRef = getHeightMapRef(x, z);
        int h = area.heightmap[hmRef] + 1;

        int ref = getRef(x, h, z);
        for (int y = 0; y <= (area.maxY - h); y++) {
          int r = ref + (y * MAX_Y_OFFSET);
          area.light[r] = (byte) ((area.light[r] & 0xF) | (max << 4));
        }

        lightQueue.add(new LightNode(x + area.minBlockX, h, z + area.minBlockZ, max));
      }
    }
    propagateSunlight(lightQueue, worldSection);
    worldSection.unlock();
  }

  private static void propagateSunlight(ArrayDeque<LightNode> lightQueue, LightWorldSection w) {
    if (lightQueue.isEmpty()) return;

    while (!lightQueue.isEmpty()) {
      LightNode n = lightQueue.pop();
      int x = n.x;
      int y = n.y;
      int z = n.z;
      int l = n.l;

      if (l <= 1) continue;

      tryPropagateSunlight(lightQueue, w, x - 1, y, z, l, l - 1);
      tryPropagateSunlight(lightQueue, w, x + 1, y, z, l, l - 1);
      tryPropagateSunlight(lightQueue, w, x, y, z - 1, l, l - 1);
      tryPropagateSunlight(lightQueue, w, x, y, z + 1, l, l - 1);
      if (y > 0) tryPropagateSunlight(lightQueue, w, x, y - 1, z, l, l - 1);
      if (y < w.maxY(x, z)) tryPropagateSunlight(lightQueue, w, x, y + 1, z, l, l); // go down without loss in strength
    }
  }

  private static void tryPropagateSunlight(ArrayDeque<LightNode> lightQueue, LightWorldSection w, int x, int y, int z, int l, int ln) {
    int dX = CoordinateConverter.area(x) - w.initialAreaX;
    int dZ = CoordinateConverter.area(z) - w.initialAreaZ;
    Area a = w.areas[dX + 1][dZ + 1];
    int ref = getRef(x - a.minBlockX, y, z - a.minBlockZ);
    if (ref >= a.light.length) return; //FIXME
    if (!transparent(a, ref)) return;
    int i = ((a.light[ref] >> 4) & 0xF);
    if (i + 2 <= l) {
      a.light[ref] = (byte) ((a.light[ref] & 0xF) | (ln << 4));
      lightQueue.add(new LightNode(x, y, z, ln));
    }
  }
}

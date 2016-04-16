package ethanjones.cubes.world.light;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.storage.Area;

import java.util.ArrayDeque;

import static ethanjones.cubes.world.storage.Area.*;

public class BlockLight {

  public static final byte FULL_LIGHT = (byte) 0xFF;

  public static void spreadLight(int x, int y, int z) {
    long ms = System.currentTimeMillis();

    Area area = Sided.getCubes().world.getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (y >= 0 && y <= area.maxY) {
      ArrayDeque<LightNode> lightQueue = new ArrayDeque<LightNode>(1000);
      LightWorldSection w = new LightWorldSection(area, y / SIZE_BLOCKS);

      if (y <= w.maxY(x + 1, z) && (w.transparent(x + 1, y, z) || w.isLightSource(x + 1, y, z)))
        lightQueue.add(new LightNode(x + 1, y, z, w.getLight(x + 1, y, z)));
      if (y <= w.maxY(x - 1, z) && (w.transparent(x - 1, y, z) || w.isLightSource(x - 1, y, z)))
        lightQueue.add(new LightNode(x - 1, y, z, w.getLight(x - 1, y, z)));

      if (y < w.maxY(x, z) && (w.transparent(x, y + 1, z) || w.isLightSource(x, y + 1, z)))
        lightQueue.add(new LightNode(x, y + 1, z, w.getLight(x, y + 1, z)));
      if (y > 0 && (w.transparent(x, y - 1, z) || w.isLightSource(x, y - 1, z)))
        lightQueue.add(new LightNode(x, y - 1, z, w.getLight(x, y - 1, z)));

      if (y <= w.maxY(x, z + 1) && (w.transparent(x, y, z + 1) || w.isLightSource(x, y, z + 1)))
        lightQueue.add(new LightNode(x, y, z + 1, w.getLight(x, y, z + 1)));
      if (y <= w.maxY(x, z - 1) && (w.transparent(x, y, z - 1) || w.isLightSource(x, y, z - 1)))
        lightQueue.add(new LightNode(x, y, z - 1, w.getLight(x, y, z - 1)));

      propagateAdd(lightQueue, w);
      w.unlock();
    }
    Log.debug("Light spread: " + (System.currentTimeMillis() - ms) + "ms");
  }

  public static void addLight(int x, int y, int z, int l) {
    long ms = System.currentTimeMillis();

    Area area = Sided.getCubes().world.getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (y > 0 && y <= area.maxY) {
      ArrayDeque<LightNode> lightQueue = new ArrayDeque<LightNode>(1000);
      LightWorldSection lightWorldSection = new LightWorldSection(area, y / SIZE_BLOCKS);

      area.setLight(x - area.minBlockX, y, z - area.minBlockZ, l);
      lightQueue.add(new LightNode(x, y, z, l));
      propagateAdd(lightQueue, lightWorldSection);
      lightWorldSection.unlock();
    }
    Log.debug("Light add: " + (System.currentTimeMillis() - ms) + "ms");
  }

  private static void propagateAdd(ArrayDeque<LightNode> lightQueue, LightWorldSection w) {
    if (lightQueue.isEmpty()) return;

    while (!lightQueue.isEmpty()) {
      LightNode n = lightQueue.pop();
      int x = n.x;
      int y = n.y;
      int z = n.z;
      int l = n.l;

      if (l <= 1) continue;

      tryPropagateAdd(lightQueue, w, x - 1, y, z, l);
      tryPropagateAdd(lightQueue, w, x + 1, y, z, l);
      tryPropagateAdd(lightQueue, w, x, y, z - 1, l);
      tryPropagateAdd(lightQueue, w, x, y, z + 1, l);
      if (y > 0) tryPropagateAdd(lightQueue, w, x, y - 1, z, l);
      tryPropagateAdd(lightQueue, w, x, y + 1, z, l);
    }
  }

  private static void tryPropagateAdd(ArrayDeque<LightNode> lightQueue, LightWorldSection w, int x, int y, int z, int l) {
    int dX = CoordinateConverter.area(x) - w.initialAreaX;
    int dZ = CoordinateConverter.area(z) - w.initialAreaZ;
    Area a = w.areas[dX + 1][dZ + 1];
    int ref = getRef(x - a.minBlockX, y, z - a.minBlockZ);
    if (y > a.maxY || !w.transparent(a, ref)) return;
    if ((a.light[ref] & 0xF) + 2 <= l) {
      a.light[ref] = (byte) ((a.light[ref] & 0xF0) | (l - 1));
      lightQueue.add(new LightNode(x, y, z, l - 1));
    }
  }

  public static void removeLight(int x, int y, int z) {
    long ms = System.currentTimeMillis();

    Area area = Sided.getCubes().world.getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (y > 0 && y <= area.maxY) {
      ArrayDeque<LightNode> removeQueue = new ArrayDeque<LightNode>(1000);
      ArrayDeque<LightNode> addQueue = new ArrayDeque<LightNode>(1000);
      LightWorldSection lightWorldSection = new LightWorldSection(area, y / SIZE_BLOCKS);

      int prev = area.getLight(x - area.minBlockX, y, z - area.minBlockZ);
      area.setLight(x - area.minBlockX, y, z - area.minBlockZ, 0);
      removeQueue.add(new LightNode(x, y, z, prev));
      propagateRemove(removeQueue, addQueue, lightWorldSection);
      propagateAdd(addQueue, lightWorldSection);
      lightWorldSection.unlock();
    }
    Log.debug("Light remove: " + (System.currentTimeMillis() - ms) + "ms");
  }

  private static void propagateRemove(ArrayDeque<LightNode> removeQueue, ArrayDeque<LightNode> addQueue, LightWorldSection w) {
    if (removeQueue.isEmpty()) return;

    while (!removeQueue.isEmpty()) {
      LightNode n = removeQueue.pop();
      int x = n.x;
      int y = n.y;
      int z = n.z;
      int l = n.l;

      if (l <= 1) continue;

      tryPropagateRemove(removeQueue, addQueue, w, x - 1, y, z, l);
      tryPropagateRemove(removeQueue, addQueue, w, x + 1, y, z, l);
      tryPropagateRemove(removeQueue, addQueue, w, x, y, z - 1, l);
      tryPropagateRemove(removeQueue, addQueue, w, x, y, z + 1, l);
      if (y > 0) tryPropagateRemove(removeQueue, addQueue, w, x, y - 1, z, l);
      tryPropagateRemove(removeQueue, addQueue, w, x, y + 1, z, l);
    }
  }

  private static void tryPropagateRemove(ArrayDeque<LightNode> removeQueue, ArrayDeque<LightNode> addQueue, LightWorldSection w, int x, int y, int z, int l) {
    int dX = CoordinateConverter.area(x) - w.initialAreaX;
    int dZ = CoordinateConverter.area(z) - w.initialAreaZ;
    Area a = w.areas[dX + 1][dZ + 1];
    int ref = getRef(x - a.minBlockX, y, z - a.minBlockZ);
    if (y > a.maxY || !w.transparent(a, ref)) return;
    int p = a.light[ref] & 0xF;
    if (p != 0 && p < l) {
      a.light[ref] = (byte) (a.light[ref] & 0xF0); // same as ((a.light[ref] & 0xF0) | 0)
      removeQueue.add(new LightNode(x, y, z, p));
    } else if (p >= l) {
      addQueue.add(new LightNode(x, y, z, p));
    }
  }

}

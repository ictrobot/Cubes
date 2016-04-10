package ethanjones.cubes.world.light;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.world.block.BlockChangedEvent;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.storage.Area;

import java.util.ArrayDeque;

import static ethanjones.cubes.world.storage.Area.*;

public class WorldLight {

  public static final byte FULL_LIGHT = (byte) 0xFF;

  public static void addLight(int x, int y, int z, int l) {
    long ms = System.currentTimeMillis();

    Area area = Sided.getCubes().world.getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (y > 0 && y <= area.maxY) {
      ArrayDeque<LightNode> lightQueue = new ArrayDeque<LightNode>(1000);
      WorldSection worldSection = new WorldSection(area, y / SIZE_BLOCKS);

      area.setLight(x, y, z, l);
      lightQueue.add(new LightNode(x, y, z, l));
      propagateAdd(lightQueue, worldSection);
      worldSection.unlock();
    }
    Log.debug("Light add: " + (System.currentTimeMillis() - ms) + "ms");
  }

  private static void propagateAdd(ArrayDeque<LightNode> lightQueue, WorldSection w) {
    if (lightQueue.isEmpty()) return;
    boolean first = true;

    while (!lightQueue.isEmpty()) {
      LightNode n = lightQueue.pop();
      int x = n.x;
      int y = n.y;
      int z = n.z;
      int l = n.l;

      if (l <= 1 || (!first && !w.transparent(x, y, z))) continue;
      first = false;

      tryPropagateAdd(lightQueue, w, x - 1, y, z, l);
      tryPropagateAdd(lightQueue, w, x + 1, y, z, l);
      tryPropagateAdd(lightQueue, w, x, y, z - 1, l);
      tryPropagateAdd(lightQueue, w, x, y, z + 1, l);
      if (y > 0) tryPropagateAdd(lightQueue, w, x, y - 1, z, l);
      if (y < w.maxY(x, z)) tryPropagateAdd(lightQueue, w, x, y + 1, z, l);
    }
  }

  private static void tryPropagateAdd(ArrayDeque<LightNode> lightQueue, WorldSection w, int x, int y, int z, int l) {
    int dX = CoordinateConverter.area(x) - w.initialAreaX;
    int dZ = CoordinateConverter.area(z) - w.initialAreaZ;
    Area a = w.areas[dX + 1][dZ + 1];
    int ref = getRef(x - a.minBlockX, y, z - a.minBlockZ);
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
      WorldSection worldSection = new WorldSection(area, y / SIZE_BLOCKS);

      int prev = area.getLight(x, y, z);
      area.setLight(x, y, z, 0);
      removeQueue.add(new LightNode(x, y, z, prev));
      propagateRemove(removeQueue, addQueue, worldSection);
      propagateAdd(addQueue, worldSection);
      worldSection.unlock();
    }
    Log.debug("Light remove: " + (System.currentTimeMillis() - ms) + "ms");
  }

  private static void propagateRemove(ArrayDeque<LightNode> removeQueue, ArrayDeque<LightNode> addQueue, WorldSection w) {
    if (removeQueue.isEmpty()) return;
    boolean first = true;

    while (!removeQueue.isEmpty()) {
      LightNode n = removeQueue.pop();
      int x = n.x;
      int y = n.y;
      int z = n.z;
      int l = n.l;

      if (l <= 1 || (!first && !w.transparent(x, y, z))) continue;
      first = false;

      tryPropagateRemove(removeQueue, addQueue, w, x - 1, y, z, l);
      tryPropagateRemove(removeQueue, addQueue, w, x + 1, y, z, l);
      tryPropagateRemove(removeQueue, addQueue, w, x, y, z - 1, l);
      tryPropagateRemove(removeQueue, addQueue, w, x, y, z + 1, l);
      if (y > 0) tryPropagateRemove(removeQueue, addQueue, w, x, y - 1, z, l);
      if (y < w.maxY(x, z)) tryPropagateRemove(removeQueue, addQueue, w, x, y + 1, z, l);
    }
  }

  private static void tryPropagateRemove(ArrayDeque<LightNode> removeQueue, ArrayDeque<LightNode> addQueue, WorldSection w, int x, int y, int z, int l) {
    int dX = CoordinateConverter.area(x) - w.initialAreaX;
    int dZ = CoordinateConverter.area(z) - w.initialAreaZ;
    Area a = w.areas[dX + 1][dZ + 1];
    int ref = getRef(x - a.minBlockX, y, z - a.minBlockZ);
    int p = a.light[ref] & 0xF;
    if (p != 0 && p < l) {
      a.light[ref] = (byte) (a.light[ref] & 0xF0); // same as ((a.light[ref] & 0xF0) | 0)
      removeQueue.add(new LightNode(x, y, z, p));
    } else if (p >= l) {
      addQueue.add(new LightNode(x, y, z, p));
    }
  }

  private static class WorldSection {
    public final int initialAreaX;
    public final int initialAreaZ;
    public final Area[][] areas = new Area[3][3];
    private final World world;
    private final int ySection;

    private WorldSection(Area initial, int ySection) {
      this.world = initial.world;
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

//    private int getLight(int x, int y, int z) {
//      Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
//      return (a.light[getRef(x - a.minBlockX, y, z - a.minBlockZ)]) & 0xF;
//    }
//
//    private void setLight(int x, int y, int z, int l) {
//      Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
//      int ref = getRef(x - a.minBlockX, y, z - a.minBlockZ);
//      a.light[ref] = (byte) ((a.light[ref] & 0xF0) | l);
//    }

    private boolean transparent(int x, int y, int z) {
      Area a = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
      int ref = getRef(x - a.minBlockX, y, z - a.minBlockZ);
      return a.blocks[ref] == 0;
    }

    private int maxY(int x, int z) {
      return getArea(CoordinateConverter.area(x), CoordinateConverter.area(z)).maxY;
    }

    private Area getArea(int areaX, int areaZ) {
      int dX = areaX - initialAreaX;
      int dZ = areaZ - initialAreaZ;
      return areas[dX + 1][dZ + 1];
    }

    private void unlock() {
      boolean isClient = Sided.getSide() == Side.Client;
      for (Area[] areaArr : areas) {
        for (Area area : areaArr) {
          if (isClient && ySection - 1 < area.height) area.updateRender(ySection - 1);
          if (isClient && ySection < area.height) area.updateRender(ySection);
          if (isClient && ySection + 1 < area.height) area.updateRender(ySection + 1);

          area.lock.writeUnlock();
        }
      }
    }
  }

  public static class WorldLightHandler {
    @EventHandler
    public void blockChanged(BlockChangedEvent event) {
      BlockReference blockReference = event.getBlockReference();
      Block oldBlock = event.getOldBlock();
      Block newBlock = event.getNewBlock();

      if (oldBlock != null && oldBlock.getLightLevel() > 0) {
        WorldLight.removeLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
      }
      if (newBlock != null && newBlock.getLightLevel() > 0) {
        WorldLight.addLight(blockReference.blockX, blockReference.blockY, blockReference.blockZ, event.getNewBlock().getLightLevel());
      }
    }
  }

  public static class LightNode {
    public int x;
    public int y;
    public int z;
    public int l;

    public LightNode(int x, int y, int z, int l) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.l = l;
    }
  }
}

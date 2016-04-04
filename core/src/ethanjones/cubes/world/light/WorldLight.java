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

import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

import static ethanjones.cubes.world.storage.Area.*;

public class WorldLight {

  private static final int MAX = SIZE_BLOCKS - 1;
  public static final byte FULL_LIGHT = (byte) 0xFF;

  public static void addLight(int blockX, int blockY, int blockZ, int l) {
    long ms = System.currentTimeMillis();
    World world = Sided.getCubes().world;

    Area area = world.getArea(CoordinateConverter.area(blockX), CoordinateConverter.area(blockZ));
    if (blockY > 0 && blockY <= area.maxY) {
      int x = blockX - area.minBlockX;
      int y = blockY;
      int z = blockZ - area.minBlockZ;
      ArrayDeque<LightNode> lightQueue = new ArrayDeque<LightNode>();

      area.setLight(x, y, z, l);
      lightQueue.add(new LightNode(area, x, y, z, l));
      propagateAdd(lightQueue, world);
    }
    Log.debug("Light add: " + (System.currentTimeMillis() - ms) + "ms");
  }

  private static void propagateAdd(ArrayDeque<LightNode> lightQueue, World world) {
    if (lightQueue.isEmpty()) return;
    boolean isClient = Sided.getSide() == Side.Client;
    HashSet<Area> used = new HashSet<Area>();
    Area initial = lightQueue.getFirst().area;
    initial.lock.writeLock();

    boolean first = true;

    while (!lightQueue.isEmpty()) {
      LightNode n = lightQueue.pop();
      Area area = n.area;
      int x = n.x;
      int y = n.y;
      int z = n.z;
      int l = n.l;

      setLight(area, x, y, z, l);
      if (isClient) area.updateRender(y / SIZE_BLOCKS);

      if (l <= 1 || (!first && !transparent(area, x, y, z))) continue;
      first = false;

      // neg X
      if (x > 0) {
        if (getLight(area, x - 1, y, z) + 2 <= l) { // && block is opaque
          lightQueue.add(new LightNode(area, x - 1, y, z, l - 1));
        }
      } else {
        Area a = world.getArea(area.areaX - 1, area.areaZ);
        if (a.getLight(MAX, y, z) + 2 <= l) {
          if (!used.contains(a)) {
            a.lock.writeLock();
            used.add(a);
          }
          lightQueue.add(new LightNode(a, MAX, y, z, l - 1));
        }
      }
      // pos X
      if (x < MAX) {
        if (getLight(area, x + 1, y, z) + 2 <= l) {
          lightQueue.add(new LightNode(area, x + 1, y, z, l - 1));
        }
      } else {
        Area a = world.getArea(area.areaX + 1, area.areaZ);
        if (a.getLight(0, y, z) + 2 <= l) {
          if (!used.contains(a)) {
            a.lock.writeLock();
            used.add(a);
          }
          lightQueue.add(new LightNode(a, 0, y, z, l - 1));
        }
      }
      // neg Z
      if (z > 0) {
        if (getLight(area, x, y, z - 1) + 2 <= l) {
          lightQueue.add(new LightNode(area, x, y, z - 1, l - 1));
        }
      } else {
        Area a = world.getArea(area.areaX, area.areaZ - 1);
        if (a.getLight(x, y, MAX) + 2 <= l) {
          if (!used.contains(a)) {
            a.lock.writeLock();
            used.add(a);
          }
          lightQueue.add(new LightNode(a, x, y, MAX, l - 1));
        }
      }
      // pos Z
      if (z < MAX) {
        if (getLight(area, x, y, z + 1) + 2 <= l) {
          lightQueue.add(new LightNode(area, x, y, z + 1, l - 1));
        }
      } else {
        Area a = world.getArea(area.areaX, area.areaZ + 1);
        if (a.getLight(x, y, 0) + 2 <= l) {
          if (!used.contains(a)) {
            a.lock.writeLock();
            used.add(a);
          }
          lightQueue.add(new LightNode(a, x, y, 0, l - 1));
        }
      }
      // neg Y
      if (y > 0) {
        if (getLight(area, x, y - 1, z) + 2 <= l) {
          lightQueue.add(new LightNode(area, x, y - 1, z, l - 1));
        }
      }
      // pos Y
      if (y < area.maxY) {
        if (getLight(area, x, y + 1, z) + 2 <= l) {
          lightQueue.add(new LightNode(area, x, y + 1, z, l - 1));
        }
      }
    }
    for (Area area : used) {
      area.lock.writeUnlock();
    }
  }

  public static void removeLight(int blockX, int blockY, int blockZ) {
    long ms = System.currentTimeMillis();
    World world = Sided.getCubes().world;

    Area area = world.getArea(CoordinateConverter.area(blockX), CoordinateConverter.area(blockZ));
    if (blockY > 0 && blockY <= area.maxY) {
      int x = blockX - area.minBlockX;
      int y = blockY;
      int z = blockZ - area.minBlockZ;
      ArrayDeque<LightNode> removeQueue = new ArrayDeque<LightNode>();
      ArrayDeque<LightNode> addQueue = new ArrayDeque<LightNode>();

      int prev = area.getLight(x, y, z);
      area.setLight(x, y, z, 0);
      removeQueue.add(new LightNode(area, x, y, z, prev));
      propagateRemove(removeQueue, addQueue, world);
      propagateAdd(addQueue, world);
    }
    Log.debug("Light remove: " + (System.currentTimeMillis() - ms) + "ms");
  }

  private static void propagateRemove(ArrayDeque<LightNode> removeQueue, ArrayDeque<LightNode> addQueue, World world) {
    if (removeQueue.isEmpty()) return;
    boolean isClient = Sided.getSide() == Side.Client;
    ArrayList<Area> used = new ArrayList<Area>();
    boolean first = true;

    while (!removeQueue.isEmpty()) {
      LightNode n = removeQueue.pop();
      Area area = n.area;
      int x = n.x;
      int y = n.y;
      int z = n.z;
      int l = n.l;

      if (!used.contains(area)) {
        area.lock.writeLock();
        used.add(area);
      }

      area.setLight(x, y, z, 0);
      if (isClient) area.updateRender(y / SIZE_BLOCKS);

      if (l <= 1 || (!first && !transparent(area, x, y, z))) continue;
      first = false;

      // neg X
      if (x > 0) {
        int p = getLight(area, x - 1, y, z);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(area, x - 1, y, z, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(area, x - 1, y, z, p));
        }
      } else {
        Area a = world.getArea(area.areaX - 1, area.areaZ);
        int p = a.getLight(MAX, y, z); //don't use getLight(a, ...) as area not yet locked
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(a, MAX, y, z, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(a, MAX, y, z, p));
        }
      }
      // pos X
      if (x < MAX) {
        int p = getLight(area, x + 1, y, z);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(area, x + 1, y, z, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(area, x + 1, y, z, p));
        }
      } else {
        Area a = world.getArea(area.areaX + 1, area.areaZ);
        int p = a.getLight(0, y, z);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(a, 0, y, z, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(a, 0, y, z, p));
        }
      }
      // neg Z
      if (z > 0) {
        int p = getLight(area, x, y, z - 1);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(area, x, y, z - 1, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(area, x, y, z - 1, p));
        }
      } else {
        Area a = world.getArea(area.areaX, area.areaZ - 1);
        int p = a.getLight(x, y, MAX);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(a, x, y, MAX, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(a, x, y, MAX, p));
        }
      }
      // pos Z
      if (z < MAX) {
        int p = getLight(area, x, y, z + 1);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(area, x, y, z + 1, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(area, x, y, z + 1, p));
        }
      } else {
        Area a = world.getArea(area.areaX, area.areaZ + 1);
        int p = a.getLight(x, y, 0);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(a, x, y, 0, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(a, x, y, 0, p));
        }
      }
      // neg Y
      if (y > 0) {
        int p = getLight(area, x, y - 1, z);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(area, x, y - 1, z, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(area, x, y - 1, z, p));
        }
      }
      // pos Y
      if (y < area.maxY) {
        int p = getLight(area, x, y + 1, z);
        if (p != 0 && p < l) {
          removeQueue.add(new LightNode(area, x, y + 1, z, p));
        } else if (p >= l) {
          addQueue.add(new LightNode(area, x, y + 1, z, p));
        }
      }
    }
  }

  /*
  Same as methods in Area but for when the Area is already locked
   */
  private static int getLight(Area a, int x, int y, int z) {
    return (a.light[getRef(x, y, z)]) & 0xF;
  }

  private static void setLight(Area a, int x, int y, int z, int l) {
    int ref = getRef(x, y, z);
    a.light[ref] = (byte) ((a.light[ref] & 0xF0) | l);
  }

  private static boolean transparent(Area a, int x, int y, int z) {
    int ref = getRef(x, y, z);
    return a.blocks[ref] == 0;
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
}

package ethanjones.cubes.world.light;

import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.storage.Area;

import java.util.LinkedList;

import static ethanjones.cubes.world.storage.Area.*;

public class WorldLight {

  private static final int MAX = SIZE_BLOCKS - 1;

  public static void addLight(int blockX, int blockY, int blockZ, int l) {
    World world = Sided.getCubes().world;

    Area area = world.getArea(CoordinateConverter.area(blockX), CoordinateConverter.area(blockZ));
    if (blockY > 0 && blockY <= area.maxY) {
      int x = blockX - area.minBlockX;
      int y = blockY;
      int z = blockZ - area.minBlockZ;
      LinkedList<LightNode> lightQueue = new LinkedList<LightNode>();

      area.setLight(x, y, z, l);
      lightQueue.add(new LightNode(area, x, y, z, l));
      propagateAdd(lightQueue, world);
    }
  }

  private static void propagateAdd(LinkedList<LightNode> lightQueue, World world) {
    while (!lightQueue.isEmpty()) {
      LightNode n = lightQueue.pop();
      Area area = n.area;
      int x = n.x;
      int y = n.y;
      int z = n.z;
      int l = n.l;
      if (l <= 1) continue;

      // neg X
      if (x > 0) {
        if (area.getLight(x - 1, y, z) + 2 <= l) { // && block is opaque
          area.setLight(x - 1, y, z, l - 1);
          lightQueue.add(new LightNode(area, x - 1, y, z, l - 1));
        }
      } else {
        Area a = world.getArea(area.areaX - 1, area.areaZ);
        if (a.getLight(MAX, y, z) + 2 <= l) {
          a.setLight(MAX, y, z, l - 1);
          lightQueue.add(new LightNode(a, MAX, y, z, l - 1));
        }
      }
      // pos X
      if (x < MAX) {
        if (area.getLight(x + 1, y, z) + 2 <= l) {
          area.setLight(x + 1, y, z, l - 1);
          lightQueue.add(new LightNode(area, x + 1, y, z, l - 1));
        }
      } else {
        Area a = world.getArea(area.areaX + 1, area.areaZ);
        if (a.getLight(0, y, z) + 2 <= l) {
          a.setLight(0, y, z, l - 1);
          lightQueue.add(new LightNode(a, 0, y, z, l - 1));
        }
      }
      // neg Z
      if (z > 0) {
        if (area.getLight(x, y, z - 1) + 2 <= l) {
          area.setLight(x, y, z - 1, l - 1);
          lightQueue.add(new LightNode(area, x, y, z - 1, l - 1));
        }
      } else {
        Area a = world.getArea(area.areaX, area.areaZ - 1);
        if (a.getLight(x, y, MAX) + 2 <= l) {
          a.setLight(x, y, MAX, l - 1);
          lightQueue.add(new LightNode(a, x, y, MAX, l - 1));
        }
      }
      // pos Z
      if (z < MAX) {
        if (area.getLight(x, y, z + 1) + 2 <= l) {
          area.setLight(x, y, z + 1, l - 1);
          lightQueue.add(new LightNode(area, x, y, z + 1, l - 1));
        }
      } else {
        Area a = world.getArea(area.areaX, area.areaZ + 1);
        if (a.getLight(x, y, 0) + 2 <= l) {
          a.setLight(x, y, 0, l - 1);
          lightQueue.add(new LightNode(a, x, y, 0, l - 1));
        }
      }
      // neg Y
      if (y > 0) {
        if (area.getLight(x, y - 1, z) + 2 <= l) {
          area.setLight(x, y - 1, z, l - 1);
          lightQueue.add(new LightNode(area, x, y - 1, z, l - 1));
        }
      }
      // pos Y
      if (y < area.maxY) {
        if (area.getLight(x, y + 1, z) + 2 <= l) {
          area.setLight(x, y + 1, z, l - 1);
          lightQueue.add(new LightNode(area, x, y - 1, z, l - 1));
        }
      }
    }
  }
}

package ethanjones.cubes.world.storage;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.core.util.Lock.HasLock;
import ethanjones.cubes.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class AreaMap implements Iterable<Area>, HasLock {
  
  public static final int AREA_NODE_SIZE = 9;
  
  public final ArrayList<AreaNode> nodes = new ArrayList<AreaNode>();
  public final Lock lock = new Lock();
  public final World world;
  
  public AreaMap(World world) {
    this.world = world;
  }
  
  private AreaNode getAreaNode(int areaX, int areaZ, boolean write) {
    int minAreaX = ((int) Math.floor(((double) areaX) / AREA_NODE_SIZE)) * AREA_NODE_SIZE;
    int minAreaZ = ((int) Math.floor(((double) areaZ) / AREA_NODE_SIZE)) * AREA_NODE_SIZE;
    Iterator<AreaNode> iterator = nodes.iterator();
    while (iterator.hasNext()) {
      AreaNode node = iterator.next();
      if (node.minAreaX == minAreaX && node.minAreaZ == minAreaZ) return node;
      if (write && node.storedAreas == 0) iterator.remove();
    }
    AreaNode newNode = new AreaNode(minAreaX, minAreaZ);
    nodes.add(newNode);
    return newNode;
  }
  
  private void removeNode(AreaNode node) {
    nodes.remove(node);
  }
  
  public Area getArea(int areaX, int areaZ) {
    lock.readLock();
    AreaNode node = getAreaNode(areaX, areaZ, false);
    int nX = areaX - node.minAreaX;
    int nZ = areaZ - node.minAreaZ;
    int n = nX + nZ * AREA_NODE_SIZE;
    Area area = node.areas[n];
    lock.readUnlock();
    return area;
  }
  
  public Area lockedGetArea(int areaX, int areaZ) {
    AreaNode node = getAreaNode(areaX, areaZ, false);
    return node.areas[(areaX - node.minAreaX) + (areaZ - node.minAreaZ) * AREA_NODE_SIZE];
  }
  
  public Area setArea(int areaX, int areaZ, Area area) {
    lock.writeLock();
    AreaNode node = getAreaNode(areaX, areaZ, true);
    int nX = areaX - node.minAreaX;
    int nZ = areaZ - node.minAreaZ;
    int n = nX + nZ * AREA_NODE_SIZE;
    Area old = node.areas[n];
    if (old == area) {
      lock.writeUnlock();
      return old;
    }
    node.areas[n] = area;
    if (old == null && area != null) node.storedAreas++;
    if (old != null && area == null) node.storedAreas--;
    if (node.storedAreas == 0) removeNode(node);
    lock.writeUnlock();
    if (area != null) area.setAreaMap(this);
    if (old != null) {
      old.setAreaMap(null);
      old.unload();
    }
    return old;
  }
  
  public Collection<Area> storeInCollection(Collection<Area> collection) {
    lock.readLock();
    for (AreaNode node : nodes) {
      for (Area area : node.areas) {
        if (area == null) continue;
        collection.add(area);
      }
    }
    lock.readUnlock();
    return collection;
  }
  
  @Override
  public AreaIterator iterator() {
    if (!lock.readLocked() && !lock.ownedByCurrentThread()) throw new CubesException("AreaMap should be read locked (write for remove)");
    return new AreaIterator();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    lock.readLock();
    for (Area area : this) {
      stringBuilder.append(area.toString()).append(" ");
    }
    lock.readUnlock();
    return stringBuilder.toString();
  }
  
  public int getSize() {
    int counter = 0;
    lock.readLock();
    for (AreaNode node : nodes) {
      counter += node.storedAreas;
    }
    lock.readUnlock();
    return counter;
  }
  
  public void empty() {
    nodes.clear();
  }
  
  @Override
  public Lock getLock() {
    return lock;
  }
  
  public class AreaNode {
    private Area[] areas = new Area[AREA_NODE_SIZE * AREA_NODE_SIZE]; //nX + nZ * AREA_NODE_SIZE
    private int minAreaX, minAreaZ, maxAreaX, maxAreaZ, storedAreas;
    private AreaMap areaMap;
    
    public AreaNode(int minAreaX, int minAreaZ) {
      this.minAreaX = minAreaX;
      this.minAreaZ = minAreaZ;
      this.maxAreaX = minAreaX + AREA_NODE_SIZE;
      this.maxAreaZ = minAreaX + AREA_NODE_SIZE;
      this.storedAreas = 0;
      this.areaMap = AreaMap.this;
    }
  }
  
  public class AreaIterator implements Iterator<Area> {
    private Iterator<AreaNode> areaNodeIterator = ((ArrayList<AreaNode>) nodes.clone()).iterator();
    private AreaNode current = null;
    private int currentN = 0;
  
    @Override
    public boolean hasNext() {
      int n = currentN + 1;
      while (true) {
        if (current == null) {
          if (areaNodeIterator.hasNext()) {
            current = areaNodeIterator.next();
            currentN = -1;
            n = 0;
          } else {
            return false;
          }
        } else {
          for (int i = n; i < (AREA_NODE_SIZE * AREA_NODE_SIZE); i++) {
            Area area = current.areas[i];
            if (area != null) return true;
          }
          current = null;
        }
      }
    }
  
    @Override
    public Area next() {
      while (true) {
        if (current == null) {
          if (areaNodeIterator.hasNext()) {
            current = areaNodeIterator.next();
            currentN = -1;
          } else {
            return null;
          }
        } else {
          currentN++;
          for ( ; currentN < (AREA_NODE_SIZE * AREA_NODE_SIZE); currentN++) {
            Area area = current.areas[currentN];
            if (area != null) return area;
          }
          current = null;
        }
      }
    }
  
    @Override
    public void remove() {
      if (!lock.ownedByCurrentThread()) throw new CubesException("AreaMap should be write locked");
      if (current == null) throw new IllegalStateException();
      Area old = current.areas[currentN];
      
      current.areas[currentN] = null;
      current.storedAreas--;
      if (current.storedAreas == 0) {
        // removeNode(current)
        current = null;
        currentN = -1;
      }
  
      if (old != null) {
        old.setAreaMap(null);
        old.unload();
      }
    }
  }
}
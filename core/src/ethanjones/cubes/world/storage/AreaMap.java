package ethanjones.cubes.world.storage;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.core.util.Lock.HasLock;
import ethanjones.cubes.world.World;

import com.badlogic.gdx.utils.LongMap;

import java.util.Iterator;

public class AreaMap implements Iterable<Area>, HasLock {

  private LongMap<Area> map = new LongMap<Area>();
  public final Lock lock = new Lock();
  public final World world;

  // long packed = ((long)x) << 32 | y & 0xFFFFFFFFL;
  // long x = (int) (packed >> 32);
  // long y = (int) (packed & 0xFFFFFFFFL);

  public AreaMap(World world) {
    this.world = world;
  }

  public Area getArea(int areaX, int areaZ) {
    lock.readLock();
    long packed = ((long)areaX) << 32 | areaZ & 0xFFFFFFFFL;
    Area area = map.get(packed, null);
    lock.readUnlock();
    return area;
  }
  
  public Area lockedGetArea(int areaX, int areaZ) {
    long packed = ((long)areaX) << 32 | areaZ & 0xFFFFFFFFL;
    return map.get(packed, null);
  }
  
  public boolean setArea(int areaX, int areaZ, Area area) {
    lock.writeLock();
    long packed = ((long)areaX) << 32 | areaZ & 0xFFFFFFFFL;
    Area old;
    if (area == null) {
      old = map.remove(packed);
    } else {
      old = map.put(packed, area);
    }
    lock.writeUnlock();

    if (old == area) return false;

    if (area != null) area.setAreaMap(this);
    if (old != null) {
      old.setAreaMap(null);
      old.unload();
    }

    return true;
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
    lock.readLock();
    int size = map.size;
    lock.readUnlock();
    return size;
  }
  
  public void empty() {
    map.clear();
  }
  
  @Override
  public Lock getLock() {
    return lock;
  }

  private class AreaIterator implements Iterator<Area> {
    private LongMap.Entries<Area> entries = new LongMap.Entries<Area>(map);
    private Area current;

    @Override
    public boolean hasNext() {
      return entries.hasNext;
    }

    @Override
    public Area next() {
      return current = entries.next().value;
    }

    @Override
    public void remove() {
      if (!lock.ownedByCurrentThread()) throw new CubesException("AreaMap should be write locked");
      if (current == null) throw new IllegalStateException();
      entries.remove();

      if (current != null) {
        current.setAreaMap(null);
        current.unload();
      }
      current = null;
    }
  }
}
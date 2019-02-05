package ethanjones.cubes.world.storage;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.locks.Locked;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.thread.WorldLockable;

import com.badlogic.gdx.utils.LongMap;

import java.util.*;

public class AreaMap extends WorldLockable implements Iterable<Area> {

  private final LongMap<Area> map = new LongMap<>(1024);
  private final ArrayList<Area> sorted = new ArrayList<>();
  private volatile boolean modifiedSinceSort = false;
  public final World world;

  // long packed = ((long)x) << 32 | y & 0xFFFFFFFFL;
  // long x = (int) (packed >> 32);
  // long y = (int) (packed & 0xFFFFFFFFL);

  public AreaMap(World world) {
    super(Type.AREAMAP, world.side);
    this.world = world;
  }
  
  public Area getArea(int areaX, int areaZ) {
    try (Locked<WorldLockable> locked = acquireReadLock()) {
      long packed = ((long)areaZ) << 32 | areaX & 0xFFFFFFFFL;
      return map.get(packed);
    }
  }
  
  public Area lockedGetArea(int areaX, int areaZ) {
    long packed = ((long)areaZ) << 32 | areaX & 0xFFFFFFFFL;
    return map.get(packed);
  }
  
  public boolean setArea(int areaX, int areaZ, Area area) {
    Area old;

    try (Locked<WorldLockable> locked = acquireWriteLock()) {
      long packed = ((long) areaZ) << 32 | areaX & 0xFFFFFFFFL;
      if (area == null) {
        old = map.remove(packed);
        if (old != null) {
          sorted.remove(old);
          modifiedSinceSort = true;
        }
      } else {
        old = map.put(packed, area);
        if (old != area) {
          if (old != null) {
            int idx = sorted.indexOf(old);
            sorted.set(idx, area);
          } else {
            sorted.add(area);
            modifiedSinceSort = true;
          }
        }
      }
    }

    if (old == area) return false;

    if (area != null) area.setAreaMap(this);
    if (old != null) {
      old.setAreaMap(null);
      old.unload();
    }

    synchronized (this) {
      this.notifyAll();
    }
    return true;
  }
  
  @Override
  public AreaIterator iterator() {
    if (!lockOwnedByCurrentThread()) throw new CubesException("AreaMap must be write locked to iterate");
    if (modifiedSinceSort) {
      Collections.sort(sorted, Area.LOCK_ITERATION_ORDER);
      modifiedSinceSort = false;
    }
    return new AreaIterator();
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    try (Locked<WorldLockable> locked = acquireReadLock()) {
      for (Area area : this) {
        stringBuilder.append(area.toString()).append(" ");
      }
    }
    return stringBuilder.toString();
  }
  
  public int getSize() {
    try (Locked<WorldLockable> locked = acquireReadLock()) {
      return map.size;
    }
  }
  
  public void empty() {
    map.clear();
  }

  private class AreaIterator implements Iterator<Area> {
    private Iterator<Area> iterator = sorted.iterator();
    private Area current = null;

    @Override
    public boolean hasNext() {
      return iterator.hasNext();
    }

    @Override
    public Area next() {
      return current = iterator.next();
    }

    @Override
    public void remove() {
      if (!lockOwnedByCurrentThread()) throw new CubesException("AreaMap should be write locked");

      if (current == null) throw new IllegalStateException();
      long packed = ((long) current.areaZ) << 32 | current.areaX & 0xFFFFFFFFL;
      iterator.remove();
      map.remove(packed);

      synchronized (this) {
        this.notifyAll();
      }
      if (current != null) {
        current.setAreaMap(null);
        current.unload();
      }
      current = null;
    }
  }
}
package ethanjones.cubes.world.storage;

import com.badlogic.gdx.utils.LongMap;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.locks.Locked;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.thread.WorldLockable;

import java.util.Iterator;

public class AreaMap extends WorldLockable implements Iterable<Area> {

  private LongMap<Area> map = new LongMap<Area>();
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
      long packed = ((long)areaX) << 32 | areaZ & 0xFFFFFFFFL;
      return map.get(packed, null);
    }
  }
  
  public Area lockedGetArea(int areaX, int areaZ) {
    long packed = ((long)areaX) << 32 | areaZ & 0xFFFFFFFFL;
    return map.get(packed, null);
  }
  
  public boolean setArea(int areaX, int areaZ, Area area) {
    Area old;

    try (Locked<WorldLockable> locked = acquireWriteLock()) {
      long packed = ((long) areaX) << 32 | areaZ & 0xFFFFFFFFL;
      if (area == null) {
        old = map.remove(packed);
      } else {
        old = map.put(packed, area);
      }
    }

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
    if (!isReadLocked() && !lockOwnedByCurrentThread()) throw new CubesException("AreaMap should be read locked (write for remove)");
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
      if (!lockOwnedByCurrentThread()) throw new CubesException("AreaMap should be write locked");
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
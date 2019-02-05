package ethanjones.cubes.core.util.locks;

import ethanjones.cubes.world.storage.Area;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RollingAreaLocked implements Iterator<Area>, Iterable<Area>, Locked<Area> {

  private final Iterator<Area> iterator;
  private Area[] all;
  private LockedRoot<Area> locked;
  private boolean alive = true;

  // 0 x-1 z-1 | 3 x   z-1 | 6 x+1 z-1
  // ----------------------------------
  // 1 x-1 z   | 4 x   z   | 7 x+1 z
  // ----------------------------------
  // 2 x-1 z+1 | 5 x   z+1 | 8 x+1 z+1

  public RollingAreaLocked(Iterator<Area> iterator) {
    this.iterator = iterator;
    this.all = new Area[9];
  }

  @Override
  public boolean hasNext() {
    return alive && iterator.hasNext();
  }

  @Override
  public Area next() {
    if (!alive) throw new NoSuchElementException();
    Area next = iterator.next();
    if (next == null) {
      if (locked != null) locked.unlock();
      throw new IllegalStateException();
    } else if (all[4] == null || all[7] != next) { // first call || not following
      // if first item or doesn't follow lock normally
      if (locked != null) locked.unlock();
      for (int i = 0; i < 9; i++) {
        all[i] = next.neighbour(next.areaX + (i / 3) - 1, next.areaZ + (i % 3) - 1);
      }
      locked = (LockedRoot<Area>) LockManager.lockMany(true, all);
    } else {
      // remove and unlock areas at x-1
      for (int i = 0; i < 3; i++) {
        if (all[i] != null) ((Lockable) locked.locks.remove(0)).lock.writeLock().unlock();
      }
      // decrement x value of x,x+1
      System.arraycopy(all, 3, all, 0, 6);
      // get new areas
      for (int i = -1; i < 2; i++) {
        all[7+i] = next.neighbour(next.areaX + 1, next.areaZ + i);
        if (all[7+i] != null) locked.extendLock(all[7+i]);
      }
    }
    return next;
  }

  @Override
  public void remove() {
    if (!alive) throw new IllegalStateException();
    iterator.remove();
  }

  @Override
  public void extendLock(Area area) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsLock(Area area) {
    if (!alive) throw new IllegalStateException();

    for (Area a : all) {
      if (area == a) return true;
    }
    return false;
  }

  @Override
  public boolean isWriteLock() {
    return true;
  }

  @Override
  public void unlock() {
    if (!alive) throw new IllegalStateException();
    for (int i = 0; i < all.length; i++) {
      all[i] = null;
    }
    if (locked != null) {
      locked.unlock();
      locked = null;
    }
    alive = false;
  }

  @Override
  public void close() {
    unlock();
  }

  @Override
  public Iterator<Area> iterator() {
    return this;
  }
}

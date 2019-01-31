package ethanjones.cubes.core.util.locks;

import java.util.LinkedList;

/**
 * Normal Locked instance
 */
public final class LockedRoot<T extends Lockable<T>> implements Locked<T> {
  public final boolean write;
  private final LockManager<T> manager;
  final LinkedList<T> locks;
  int state = 0; // 0 locked, -1 dead, -2 never alive

  LockedRoot(T initialLock, boolean write, LockManager<T> manager) {
    this.write = write;
    this.manager = manager;

    this.locks = new LinkedList<>();
    this.locks.add(initialLock);
  }

  @Override
  public void extendLock(T t) {
    if (state != 0) throw new LockException("Tried to extend dead " + t.getClass().getSimpleName() + " lock");

    int c = locks.getLast().compareTo(t);
    if (c == 0) return;
    if (c > 0) throw new LockException("Tried to extend " + t.getClass().getSimpleName() + " lock in wrong direction!");

    this.locks.add(t);
    (write ? t.lock.writeLock() : t.lock.readLock()).lock();
  }

  @Override
  public boolean containsLock(T t) {
    return locks.contains(t);
  }

  @Override
  public boolean isWriteLock() {
    return write;
  }

  @Override
  public void unlock() {
    manager.unlock(this);
  }

  @Override
  public void close() {
    unlock();
  }
}

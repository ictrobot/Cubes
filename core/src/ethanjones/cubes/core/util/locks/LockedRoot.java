package ethanjones.cubes.core.util.locks;

import java.util.ArrayList;

/**
 * Normal Locked instance
 */
final class LockedRoot<T extends Lockable<T>> implements Locked<T> {
  private final LockManager<T> manager;
  final LockedSub<T> subLock;
  final ArrayList<T> locks;
  boolean write;
  boolean alive;

  LockedRoot(LockManager<T> manager) {
    this.manager = manager;
    this.subLock = new LockedSub<>(this);
    this.locks = new ArrayList<>();
  }

  void reset() {
    if (!alive) throw new LockException("Lock already dead");
    this.write = false;
    this.alive = false;
    this.locks.clear();
  }

  void setup(T t, boolean write) {
    if (alive) throw new LockException("Lock must be unlocked first");
    this.write = write;
    this.alive = true;
    this.locks.add(t);
  }

  @Override
  public void extendLock(T t) {
    if (!alive) throw new LockException("Tried to extend dead " + t.getClass().getSimpleName() + " lock");

    int c = locks.get(locks.size() - 1).compareTo(t);
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

package ethanjones.cubes.core.util.locks;

/**
 * Each thread can only have one Locked instance at a time for each Lock Manager (excluding any Sub Locks).
 * Locked instances can be extended provided the ordering is followed.
 */
public final class LockManager<T extends Lockable<T>> {

  private final ThreadLocal<Locked<T>> threadData = new ThreadLocal<>();

  /** Used by Lockable to acquire a Locked instance */
  Locked<T> lock(T t, boolean write) {
    Locked<T> locked = threadData.get();
    if (locked != null) {
      // sub locking: grant fake locks
      if (locked.containsLock(t)) {
        if (write && !locked.isWriteLock()) throw new LockException("Tried to write lock instance of " + t.getClass().getSimpleName() + " which is part of parent read lock");
        return new LockedSub<>(locked);
      }
      throw new LockException("Tried to lock instance of " + t.getClass().getSimpleName() + " when there is another lock that doesn't contain it");
    }

    locked = new LockedRoot<>(t, write, this);
    (write ? t.lock.writeLock() : t.lock.readLock()).lock();
    threadData.set(locked);
    return locked;
  }

  Locked<T> tryLock(T t, boolean write) {
    if (threadData.get() != null) return null;

    LockedRoot<T> locked = new LockedRoot<>(t, write, this);
    boolean b = (write ? t.lock.writeLock() : t.lock.readLock()).tryLock();
    if (b) {
      threadData.set(locked);
      return locked;
    } else {
      locked.state = -2;
      return null;
    }
  }

  /** Used by LockedRoot to release a lock */
  void unlock(LockedRoot<T> locked) {
    if (locked.state < 0) throw new LockException("Tried to unlock dead lock");
    locked.state = -1;

    if (threadData.get() != locked) throw new LockException("Lock does not match!");

    for (T t : locked.locks) {
      (locked.write ? t.lock.writeLock() : t.lock.readLock()).unlock();
    }
    threadData.set(null);
  }

  /** Values must be in lock order */
  public static <T extends Lockable<T>> Locked<T> lockMany(boolean write, T... values) {
    Locked<T> locked = null;
    for (T value : values) {
      if (value != null) {
        if (locked != null) {
          locked.extendLock(value);
        } else {
          locked = value.acquireLock(write);
        }
      }
    }
    return locked;
  }
}

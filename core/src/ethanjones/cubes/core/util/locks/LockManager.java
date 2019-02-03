package ethanjones.cubes.core.util.locks;

/**
 * Each thread can only have one Locked instance at a time for each Lock Manager (excluding any Sub Locks).
 * Locked instances can be extended provided the ordering is followed.
 */
public final class LockManager<T extends Lockable<T>> {

  private final ThreadLocal<LockedRoot<T>> threadRootLocks = new ThreadLocal<LockedRoot<T>>() {
    @Override
    protected LockedRoot<T> initialValue() {
      return new LockedRoot<>(LockManager.this);
    }
  };

  /** Used by Lockable to acquire a Locked instance */
  Locked<T> lock(T t, boolean write) {
    LockedRoot<T> locked = threadRootLocks.get();
    if (locked.alive) {
      // sub locking: grant fake locks
      if (locked.containsLock(t)) {
        if (write && !locked.isWriteLock()) throw new LockException("Tried to write lock instance of " + t.getClass().getSimpleName() + " which is part of parent read lock");
        return locked.subLock;
      }
      throw new LockException("Tried to lock instance of " + t.getClass().getSimpleName() + " when there is another lock that doesn't contain it");
    } else {
      locked.setup(t, write);
      (write ? t.lock.writeLock() : t.lock.readLock()).lock();
      return locked;
    }
  }

  Locked<T> tryLock(T t, boolean write) {
    LockedRoot<T> locked = threadRootLocks.get();
    if (locked.alive) return null;

    boolean lockSucceeded = (write ? t.lock.writeLock() : t.lock.readLock()).tryLock();
    if (lockSucceeded) {
      locked.setup(t, write);
      return locked;
    } else {
      return null;
    }
  }

  /** Used by LockedRoot to release a lock */
  void unlock(LockedRoot<T> locked) {
    if (!locked.alive) throw new LockException("Tried to unlock dead lock");
    if (threadRootLocks.get() != locked) throw new LockException("Lock does not match!");

    for (T t : locked.locks) {
      (locked.write ? t.lock.writeLock() : t.lock.readLock()).unlock();
    }
    locked.reset();
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

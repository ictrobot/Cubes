package ethanjones.cubes.core.util.locks;

import java.util.concurrent.locks.ReentrantReadWriteLock;

// Usage:
//
// class Test extends Lockable<Test> {
//
//   private static final LockManager<Test> LOCK_MANAGER = new LockManager<>();
//
//   public Test() {
//     super(LOCK_MANAGER);
//   }
//
//   @Override
//   public int compareTo(Test o) {
//     ...
//   }
// }
//

public abstract class Lockable<T extends Lockable<T>> implements Comparable<T> {
  private final LockManager<T> manager;
  final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public Lockable(LockManager<T> manager) {
    this.manager = manager;
  }

  public Locked<T> acquireWriteLock() {
    return acquireLock(true);
  }

  public Locked<T> acquireReadLock() {
    return acquireLock(false);
  }

  public Locked<T> acquireLock(boolean write) {
    @SuppressWarnings("unchecked") T t = (T) this;
    return manager.lock(t, write);
  }

  public Locked<T> tryAcquireLock(boolean write) {
    @SuppressWarnings("unchecked") T t = (T) this;
    return manager.tryLock(t, write);
  }

  public boolean lockOwnedByCurrentThread() {
    return lock.isWriteLockedByCurrentThread();
  }

  public boolean isReadLocked() {
    return lock.getReadHoldCount() > 0;
  }
}

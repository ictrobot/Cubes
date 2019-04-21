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

  public Lockable(LockManager<T> manager) {
    this.manager = manager;
  }

  public Locked<T> acquireWriteLock() {
    return manager.fakeLock;
  }

  public Locked<T> acquireReadLock() {
    return manager.fakeLock;
  }

  public Locked<T> acquireLock(boolean write) {
    return manager.fakeLock;
  }

  public Locked<T> tryAcquireLock(boolean write) {
    return manager.fakeLock;
  }

  public boolean lockOwnedByCurrentThread() {
    return true;
  }

  public boolean isReadLocked() {
    return true;
  }
}

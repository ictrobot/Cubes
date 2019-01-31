package ethanjones.cubes.core.util.locks;

/**
 * If a lock is requested that it is currently locked by the same thread then a Sub Lock is returned.
 * This allows methods that lock to call methods that lock (provided the 2nd method wants to lock something
 * already locked by the 1st)
 */
public final class LockedSub<T extends Lockable<T>> implements Locked<T> {
  private final Locked<T> parent;

  LockedSub(Locked<T> parent) {
    this.parent = parent;
  }

  @Override
  public void extendLock(T t) {
    if (!parent.containsLock(t)) throw new LockException("Extension not in parent lock");
  }

  @Override
  public boolean containsLock(T t) {
    return parent.containsLock(t);
  }

  @Override
  public boolean isWriteLock() {
    return parent.isWriteLock();
  }

  @Override
  public void unlock() {

  }

  @Override
  public void close() {

  }
}

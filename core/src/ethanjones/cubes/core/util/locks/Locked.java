package ethanjones.cubes.core.util.locks;

/**
 * Normally used in a try with resources. Allows you to extend the lock provided you follow the order and unlock the lock.
 */
public interface Locked<T extends Lockable<T>> extends AutoCloseable {

  public void extendLock(T t);

  public boolean containsLock(T t);

  public boolean isWriteLock();

  public void unlock();

  @Override
  public void close();
}

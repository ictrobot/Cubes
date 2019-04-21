package ethanjones.cubes.core.util.locks;

/**
 * Cubes minimized is single threaded
 */
public class FakeLock<T extends Lockable<T>> implements Locked<T> {
  @Override
  public void extendLock(Lockable lockable) {

  }

  @Override
  public boolean containsLock(Lockable lockable) {
    return true;
  }

  @Override
  public boolean isWriteLock() {
    return true;
  }

  @Override
  public void unlock() {

  }

  @Override
  public void close() {

  }
}

package ethanjones.cubes.core.util;

// singlethreaded, so does not actually lock
public class Lock {

  public Lock readLock() {
    return this;
  }

  public Lock writeLock() {
    return this;
  }

  public Lock readUnlock() {
    return this;
  }

  public Lock writeUnlock() {
    return this;
  }

  public <T> T readUnlock(T t) {
    return t;
  }

  public <T> T writeUnlock(T t) {
    return t;
  }
  
  public boolean ownedByCurrentThread() {
    return true;
  }
  
  public boolean readLocked() {
    return true;
  }
  
  public static boolean tryToLock(boolean write, HasLock lock) {
    return true;
  }

  public static void waitToLock(boolean write, HasLock lock) {
    
  }

  public static void waitToLockAll(boolean write, HasLock... locks) {
    
  }

  public static boolean lockAll(boolean write, HasLock... locks) {
    return true;
  }

  public interface HasLock {
    Lock getLock();
  }
}

package ethanjones.cubes.core.util;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Lock {
  private static final ReentrantLock multiLock = new ReentrantLock(true);

  private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
  private ReentrantReadWriteLock.ReadLock read = lock.readLock();
  private ReentrantReadWriteLock.WriteLock write = lock.writeLock();

  public Lock readLock() {
    read.lock();
    return this;
  }

  public Lock writeLock() {
    write.lock();
    return this;
  }

  public Lock readUnlock() {
    read.unlock();
    return this;
  }

  public Lock writeUnlock() {
    write.unlock();
    return this;
  }

  public <T> T readUnlock(T t) {
    read.unlock();
    return t;
  }

  public <T> T writeUnlock(T t) {
    write.unlock();
    return t;
  }

  public static void waitToLock(boolean write, HasLock lock) {
    java.util.concurrent.locks.Lock l = write ? lock.getLock().write : lock.getLock().read;
    while (true) {
      if (l.tryLock()) return;
      Thread.yield();
    }
  }

  public static void waitToLockAll(boolean write, HasLock... locks) {
    while (true) {
      if (lockAll(write, locks)) return;
      Thread.yield();
    }
  }

  public static boolean lockAll(boolean write, HasLock... locks) {
    multiLock.lock();
    int i = 0;
    boolean fail = false;

    for (; i < locks.length; i++) {
      if (locks[i] == null) continue;
      Lock l = locks[i].getLock();
      boolean b;

      if (write) b = l.write.tryLock();
      else b = l.read.tryLock();

      if (!b) {
        fail = true;
        break;
      }
    }

    if (fail) {
      for (int j = 0; j < i; j++) {
        if (locks[j] == null) continue;
        Lock l = locks[j].getLock();
        if (write) l.write.unlock();
        else l.read.unlock();
      }
    }
    multiLock.unlock();
    return !fail;
  }

  public static interface HasLock {
    public Lock getLock();
  }
}

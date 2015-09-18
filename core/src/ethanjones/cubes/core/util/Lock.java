package ethanjones.cubes.core.util;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Lock {
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
}

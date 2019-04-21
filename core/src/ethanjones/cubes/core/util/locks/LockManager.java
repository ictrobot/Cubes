package ethanjones.cubes.core.util.locks;

public final class LockManager<T extends Lockable<T>> {
  final FakeLock<T> fakeLock = new FakeLock<>();

  public static <T extends Lockable<T>> Locked<T> lockMany(boolean write, T... values) {
    for (T value : values) {
      if (value != null) {
        return value.acquireLock(write);
      }
    }
    return null;
  }
}

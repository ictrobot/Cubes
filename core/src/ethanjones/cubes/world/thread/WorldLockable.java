package ethanjones.cubes.world.thread;

import ethanjones.cubes.core.util.locks.LockManager;
import ethanjones.cubes.core.util.locks.Lockable;
import ethanjones.cubes.side.common.Side;

public class WorldLockable extends Lockable<WorldLockable> {

  private static final LockManager<WorldLockable> lock = new LockManager<>();
  private final int value;

  public WorldLockable(Type type, Side side) {
    super(lock);
    value = (side == Side.Server ? 1 << 16 : 0) + type.ordinal();
  }

  @Override
  public int compareTo(WorldLockable o) {
    return Integer.compare(value, o.value);
  }

  public enum Type {
    WORLD, AREAMAP, ENTITIES
  }
}

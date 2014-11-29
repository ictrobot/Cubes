package ethanjones.cubes.core.system;

import com.badlogic.gdx.utils.Pool;
import java.util.HashMap;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.AreaReferencePool;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.reference.BlockReferencePool;

public class Pools {

  private static final HashMap<Class, Pool> pools = new HashMap<Class, Pool>();

  static {
    registerType(AreaReference.class, new AreaReferencePool());

    registerType(BlockReference.class, new BlockReferencePool());
  }

  public static void registerType(Class c, Pool pool) {
    Object obj = pool.obtain();
    Class<?> objClass = obj.getClass();
    pool.free(obj);
    if (objClass.equals(c)) {
      synchronized (pools) {
        if (!pools.containsKey(c)) {
          pools.put(c, pool);
        }
      }
    } else {
      throw new CubesException("Calling obtain on " + pool + " does not return " + c.getName());
    }
  }

  public static <T> T obtain(Class<T> c) {
    Pool<T> pool = pool(c);
    if (pool == null) return null;
    synchronized (pool) {
      return pool.obtain();
    }
  }

  public static <T> void free(Class<T> c, T obj) {
    Pool<T> pool = pool(c);
    if (pool == null) return;
    synchronized (pool) {
      pool.free(obj);
    }
  }

  public static <T> void free(T obj) {
    Pool pool = pool(obj);
    if (pool == null) return;
    synchronized (pool) {
      pool.free(obj);
    }
  }

  //Individual types
  public static AreaReference obtainAreaReference() {
    Pool<AreaReference> pool = pool(AreaReference.class);
    if (pool == null) return null;
    synchronized (pool) {
      return pool.obtain();
    }
  }

  public static void free(AreaReference obj) {
    Pool pool = pool(AreaReference.class);
    if (pool == null) return;
    synchronized (pool) {
      pool.free(obj);
    }
  }

  public static BlockReference obtainBlockReference() {
    Pool<BlockReference> pool = pool(BlockReference.class);
    if (pool == null) return null;
    synchronized (pool) {
      return pool.obtain();
    }
  }

  public static void free(BlockReference obj) {
    Pool pool = pool(BlockReference.class);
    if (pool == null) return;
    synchronized (pool) {
      pool.free(obj);
    }
  }

  private static Pool pool(Object object) {
    Class<?> c = object.getClass();
    synchronized (pools) {
      while (c != null && !c.equals(Object.class)) {
        Pool pool = pools.get(c);
        if (pool != null) return pool;
        c = c.getSuperclass();
      }
    }
    return null;
  }

  private static <T> Pool<T> pool(Class<T> c) {
    synchronized (pools) {
      return pools.get(c);
    }
  }
  
}

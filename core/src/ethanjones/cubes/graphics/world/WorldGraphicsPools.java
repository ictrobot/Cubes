package ethanjones.cubes.graphics.world;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.graphics.world.area.AreaMesh;
import ethanjones.cubes.graphics.world.area.AreaRenderer;

import java.util.ArrayDeque;

public class WorldGraphicsPools {

  public abstract static class DisposablePool<T extends Disposable> extends Pool<T> {

    @Override
    public void free(T obj) {
      if (obj == null) throw new IllegalArgumentException("obj cannot be null");
      if (getFree() >= max) {
        obj.dispose();
      } else {
        super.free(obj);
      }
    }

    @Override
    public void freeAll(Array<T> array) {
      if (array == null) throw new IllegalArgumentException("array cannot be null");
      for (T t : array) {
        free(t);
      }
    }

    @Override
    public void clear() {
      while (getFree() > 0) {
        obtain().dispose();
      }
    }

  }

  public static final ArrayDeque<AreaRenderer> toFree = new ArrayDeque<AreaRenderer>();

  public static void init() {
    Pools.registerType(AreaRenderer.class, new Pool<AreaRenderer>() {
      @Override
      protected AreaRenderer newObject() {
        return new AreaRenderer();
      }
    });
    Pools.registerType(AreaMesh.class, new DisposablePool<AreaMesh>() {
      @Override
      protected AreaMesh newObject() {
        return new AreaMesh();
      }
    });
  }

  public static void free() {
    AreaRenderer a;
    while ((a = toFree.poll()) != null) {
      AreaRenderer.free(a);
    }
  }
}

package ethanjones.cubes.graphics.world;

import ethanjones.cubes.core.system.Pools;

import com.badlogic.gdx.utils.Pool;

import java.util.ArrayDeque;

public class WorldGraphicsPools {

  public static final ArrayDeque<AreaRenderer> toFree = new ArrayDeque<AreaRenderer>();

  public static void init() {
    Pools.registerType(AreaRenderer.class, new Pool<AreaRenderer>() {
      @Override
      protected AreaRenderer newObject() {
        return new AreaRenderer();
      }
    });
    Pools.registerType(AreaMesh.class, new Pool<AreaMesh>() {
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

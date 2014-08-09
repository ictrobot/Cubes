package ethanjones.modularworld.world;

import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.core.thread.Threads;
import ethanjones.modularworld.world.generator.WorldGenerator;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.thread.GenerateWorld;

import java.util.HashMap;

public class WorldServer extends World {

  private static class Key {
    int x, y, z;

    public Key set(AreaReference areaReference) {
      this.x = areaReference.areaX;
      this.y = areaReference.areaY;
      this.z = areaReference.areaZ;
      return this;
    }

    @Override
    public int hashCode() {
      return x ^ y ^ z;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Key) {
        Key key = (Key) obj;
        return key.x == x && key.y == y && key.z == z;
      }
      return false;
    }
  }

  private static class KeyPool extends Pool<Key> {
    @Override
    public synchronized Key newObject() {
      return new Key();
    }
  }

  private final WorldGenerator worldGenerator;
  private HashMap<Key, Area> areaMap;
  private KeyPool keyPool;

  public WorldServer(WorldGenerator worldGenerator) {
    super();
    this.worldGenerator = worldGenerator;
    areaMap = new HashMap<Key, Area>();
    keyPool = new KeyPool();
  }

  public Area getAreaInternal(AreaReference areaReference, boolean request, boolean returnBlank) {
    Key key;
    Area area;
    synchronized (keyPool) {
      key = keyPool.obtain().set(areaReference);
    }
    synchronized (this) {
      area = areaMap.get(key);
    }
    if (area != null) {
      synchronized (keyPool) {
        keyPool.free(key);
      }
      return area;
    } else if (area == null && request) {
      requestArea(areaReference);
    }
    return returnBlank ? BLANK_AREA : null;
  }

  public boolean setAreaInternal(AreaReference areaReference, Area area) {
    Key key;
    synchronized (keyPool) {
      key = keyPool.obtain().set(areaReference);
    }
    synchronized (this) {
      areaMap.put(key, area);
    }
    return true;
  }

  public void requestArea(AreaReference areaReference) {
    setAreaInternal(areaReference, World.BLANK_AREA);
    Threads.execute(new GenerateWorld(areaReference, this));
  }

  @Override
  public void dispose() {

  }

  public WorldGenerator getWorldGenerator() {
    return worldGenerator;
  }
}

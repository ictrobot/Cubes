package ethanjones.modularworld.world;

import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.core.events.world.generation.GenerationEvent;
import ethanjones.modularworld.world.generator.WorldGenerator;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

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
      return Math.abs(x + 1) ^ Math.abs(y + 1) ^ Math.abs(z + 1);
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
    public Key newObject() {
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

  protected Area getAreaInternal(AreaReference areaReference, boolean request, boolean generatedCheck) {
    Key key = keyPool.obtain().set(areaReference);
    Area area = areaMap.get(key);
    if (area != null && (area.generated || !generatedCheck)) {
      return area;
    } else if (area == null && request) {
      requestArea(areaReference);
    }
    keyPool.free(key);
    return BLANK_AREA;
  }

  public boolean setAreaInternal(AreaReference areaReference, Area area) {
    Key key = keyPool.obtain().set(areaReference);
    areaMap.put(key, area);
    return true;
  }

  public void requestArea(AreaReference areaReference) {
    Area area = areaReference.newArea();
    worldGenerator.generate(area);
    new GenerationEvent(area, areaReference.getAreaCoordinates()).post();
    area.generated = true;
    setAreaInternal(areaReference, area);
  }

  @Override
  public void dispose() {

  }
}

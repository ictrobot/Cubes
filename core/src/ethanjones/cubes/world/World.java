package ethanjones.cubes.world;

import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.world.generation.AreaGeneratedEvent;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Pools;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.reference.multi.WorldRegion;

public abstract class World implements Disposable {

  public final Lock lock = new Lock();
  public final HashMap<AreaReference, Area> map;
  public final TerrainGenerator terrainGenerator;
  public final AtomicBoolean disposed = new AtomicBoolean(false);

  public World(TerrainGenerator terrainGenerator) {
    this.terrainGenerator = terrainGenerator;
    map = new HashMap<AreaReference, Area>(1024);
  }

  public Area setAreaInternal(Area area) {
    lock.writeLock();

    AreaReference areaReference = new AreaReference().setFromArea(area);

    if (map.containsKey(areaReference)) {
      Log.debug("World already contains " + area.areaX + "," + area.areaZ);
      return lock.writeUnlock(map.get(areaReference));
    }

    Area old = map.put(areaReference.clone(), area);

    lock.writeUnlock();

    //Must be after lock released to prevent dead locks
    if (area.features()) new AreaGeneratedEvent(area, areaReference.clone()).post();

    synchronized (map) {
      map.notifyAll();
    }

    if (old != null) old.unload();
    return area;
  }

  public Block getBlock(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null ? null : area.getBlock(x - area.minBlockX, y, z - area.minBlockZ);
  }

  public Area getArea(int areaX, int areaZ) {
    AreaReference areaReference = Pools.obtainAreaReference().setFromAreaCoordinates(areaX, areaZ);
    Area area = getArea(areaReference);
    Pools.free(AreaReference.class, areaReference);
    return area;
  }

  public Area getArea(AreaReference areaReference) {
    return getArea(areaReference, true);
  }

  public Area getArea(AreaReference areaReference, boolean request) {
    lock.readLock();
    Area area = map.get(areaReference);
    lock.readUnlock();

    if (area != null) {
      return area;
    } else if (request) {
      requestRegion(new WorldRegion(areaReference));
    }
    return null;
  }

  public abstract void requestRegion(MultiAreaReference references);

  public void setBlock(Block block, int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (area != null) area.setBlock(block, x - area.minBlockX, y, z - area.minBlockZ);
  }

  public void tick() {

  }

  public TerrainGenerator getTerrainGenerator() {
    return terrainGenerator;
  }

  @Override
  public void dispose() {
    lock.writeLock();

    disposed.set(true);

      for (Entry<AreaReference, Area> entry : map.entrySet()) {
        entry.getValue().unload();
      }
      map.clear();

    lock.writeUnlock();
  }

}

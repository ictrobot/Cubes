package ethanjones.cubes.world;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.core.gwt.FakeAtomic.AtomicBoolean;
import ethanjones.cubes.core.gwt.UUID;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.util.locks.LockManager;
import ethanjones.cubes.core.util.locks.Locked;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.generator.GeneratorManager;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.reference.multi.WorldRegion;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.AreaMap;
import ethanjones.cubes.world.storage.Entities;
import ethanjones.cubes.world.thread.GenerationTask;
import ethanjones.cubes.world.thread.WorldLockable;
import ethanjones.cubes.world.thread.WorldRequestParameter;
import ethanjones.cubes.world.thread.WorldTasks;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.utils.Disposable;

import java.util.Iterator;
import java.util.Map.Entry;

public abstract class World extends WorldLockable implements Disposable {

  public static final int MAX_TIME = (1000 / Cubes.tickMS) * 60 * 10;

  public final AreaMap map;
  public final Entities entities;
  public final TerrainGenerator terrainGenerator;
  public final Save save;
  public final Side side;

  protected int time;
  protected long playingTime;
  protected final AtomicBoolean disposed = new AtomicBoolean(false);

  public World(Save save, Side side) {
    super(Type.WORLD, side);

    this.save = save;
    this.side = side;
    this.terrainGenerator = save == null ? null : GeneratorManager.getTerrainGenerator(save.getSaveOptions());
    this.time = save == null ? 0 : save.getSaveOptions().worldTime;
    this.playingTime = save == null ? 0 : save.getSaveOptions().worldPlayingTime;
    map = new AreaMap(this);
    entities = new Entities(this);
  }
  
  public boolean setArea(Area area) {
    if (area == null) throw new IllegalArgumentException("Null");
    return setArea(area.areaX, area.areaZ, area);
  }

  public boolean setArea(int areaX, int areaZ, Area area) {
    return map.setArea(areaX, areaZ, area);
  }

  public int heightmap(int x, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null ? 0 : area.heightmap(x - area.minBlockX, z - area.minBlockZ);
  }

  public Block getBlock(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null ? null : area.getBlock(x - area.minBlockX, y, z - area.minBlockZ);
  }

  public BlockData getBlockData(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null ? null : area.getBlockData(x - area.minBlockX, y, z - area.minBlockZ);
  }

  public int getMeta(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null ? 0 : area.getMeta(x - area.minBlockX, y, z - area.minBlockZ);
  }

  public int getLight(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null || y < 0 || y > area.maxY ? 0 : area.getLight(x - area.minBlockX, y, z - area.minBlockZ);
  }

  public int getSunLight(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null || y < 0 || y > area.maxY ? 0 : area.getSunlight(x - area.minBlockX, y, z - area.minBlockZ);
  }

  public int getLightRaw(int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    return area == null || y < 0 || y > area.maxY ? 0 : area.getLightRaw(x - area.minBlockX, y, z - area.minBlockZ);
  }

  public Area getArea(int areaX, int areaZ, boolean request) {
    Area area = map.getArea(areaX, areaZ);
    if (area != null) {
      return area;
    } else if (request) {
      requestRegion(new WorldRegion(new AreaReference().setFromAreaCoordinates(areaX, areaZ)), null);
    }
    return null;
  }
  
  public Area getArea(int areaX, int areaZ) {
    return getArea(areaX, areaZ, false);
  }

  public Area getArea(AreaReference areaReference, boolean request) {
    return getArea(areaReference.areaX, areaReference.areaZ, request);
  }
  
  public Area getArea(AreaReference areaReference) {
    return getArea(areaReference.areaX, areaReference.areaZ, false);
  }

  public abstract GenerationTask requestRegion(MultiAreaReference references, WorldRequestParameter parameter);

  public void setBlock(Block block, int x, int y, int z) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (area != null) area.setBlock(block, x - area.minBlockX, y, z - area.minBlockZ, 0);
  }

  public void setBlock(Block block, int x, int y, int z, int meta) {
    Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
    if (area != null) area.setBlock(block, x - area.minBlockX, y, z - area.minBlockZ, meta);
  }

  public void setBlocks(Block block, int x1, int y1, int z1, int x2, int y2, int z2, int meta) {
    int minX = Math.min(x1, x2), minY = Math.min(y1, y2), minZ = Math.min(z1, z2);
    int maxX = Math.max(x1, x2), maxY = Math.max(y1, y2), maxZ = Math.max(z1, z2);

    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        Area area = getArea(CoordinateConverter.area(x), CoordinateConverter.area(z));
        if (area != null) {
          for (int y = minY; y <= maxY; y++) {
            area.setBlock(block, x - area.minBlockX, y, z - area.minBlockZ, meta);
          }
        }
      }
    }
  }

  public void tick() {
    try (Locked<WorldLockable> locked = LockManager.lockMany(true, this, map, entities)) {
      Iterator<Entry<UUID, Entity>> iterator = entities.map.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<UUID, Entity> entry = iterator.next();
        if (entry.getValue().update()) {
          entry.getValue().dispose();
          UUID uuid = entry.getKey();
          iterator.remove();
          removeEntity(uuid);
        }
      }
      time++;
      if (time >= MAX_TIME) time = 0;
      playingTime++;
    }
  }

  public float getWorldSunlight() {
    try (Locked<WorldLockable> locked = acquireReadLock()) {
      int t = time < MAX_TIME / 2 ? time : MAX_TIME - time;
      return ((float) t) / (((float) MAX_TIME) / 2f);
    }
  }
  
  public boolean isDay() {
    try (Locked<WorldLockable> locked = acquireReadLock()) {
      return time >= MAX_TIME / 4 && time < MAX_TIME * 3 / 4;
    }
  }

  public void setTime(int time) {
    try (Locked<WorldLockable> locked = acquireWriteLock()) {
      this.time = time % MAX_TIME;
    }
  }
  
  public int getTime() {
    try (Locked<WorldLockable> locked = acquireReadLock()) {
      return time;
    }
  }

  public TerrainGenerator getTerrainGenerator() {
    return terrainGenerator;
  }

  @Override
  public void dispose() {
    if (!disposed.compareAndSet(false, true)) return;
    
    WorldTasks.waitSaveFinish();
    try (Locked<WorldLockable> locked = LockManager.lockMany(true, this, map, entities)) {
      if (Side.isServer() || !Area.isShared()) {
        for (Area area : map) {
          area.unload();
        }
      }
      map.empty();
      for (Entity entity : entities.map.values()) {
        entity.dispose();
      }
      entities.map.clear();
    }
  }
  
  public boolean isDisposed() {
    return disposed.get();
  }

  public Entity getEntity(UUID uuid) {
    try (Locked<WorldLockable> locked = entities.acquireReadLock()) {
      return entities.map.get(uuid);
    }
  }

  public void addEntity(Entity entity) {
    try (Locked<WorldLockable> locked = entities.acquireWriteLock()) {
      entities.map.put(entity.uuid, entity);
    }
  }

  public void removeEntity(UUID uuid) {
    try (Locked<WorldLockable> locked = entities.acquireWriteLock()) {
      Entity remove = entities.map.remove(uuid);
      if (remove != null) remove.dispose();
    }
  }

  public void updateEntity(DataGroup data) {
    try (Locked<WorldLockable> locked = entities.acquireWriteLock()) {
      UUID uuid = (UUID) data.get("uuid");
      Entity entity = entities.map.get(uuid);
      if (entity != null) {
        entity.read(data);
      } else {
        Log.warning("No entity with uuid " + uuid.toString());
        addEntity(Entity.readType(data));
      }
    }
  }

  public void syncEntity(UUID uuid) {

  }
  
  public void addEntityFromSave(DataGroup dataGroup) {
    try (Locked<WorldLockable> locked = entities.acquireWriteLock()) {
      Entity entity = Entity.readType(dataGroup);
      if (entity != null && !entities.map.containsKey(entity.uuid)) {
        addEntity(entity);
      }
    }
  }

  public void save() {

  }
}

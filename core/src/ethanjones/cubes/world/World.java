package ethanjones.cubes.world;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.lua.LuaMapping;
import ethanjones.cubes.core.lua.LuaMappingWorld;
import ethanjones.cubes.core.util.Lock;
import ethanjones.cubes.core.util.Lock.HasLock;
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
import ethanjones.cubes.world.thread.WorldRequestParameter;
import ethanjones.cubes.world.thread.WorldTasks;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.utils.Disposable;
import org.luaj.vm2.LuaTable;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class World implements Disposable, HasLock {

  public static final int MAX_TIME = (1000 / Cubes.tickMS) * 60 * 10;

  public final Lock updateLock = new Lock();
  public final AreaMap map;
  public final Entities entities;
  public final TerrainGenerator terrainGenerator;
  public final Save save;
  
  protected int time;
  protected long playingTime;
  protected final AtomicBoolean disposed = new AtomicBoolean(false);
  public final LuaTable lua;

  public World(Save save) {
    this.save = save;
    this.terrainGenerator = save == null ? null : GeneratorManager.getTerrainGenerator(save.getSaveOptions());
    this.time = save == null ? 0 : save.getSaveOptions().worldTime;
    this.playingTime = save == null ? 0 : save.getSaveOptions().worldPlayingTime;
    map = new AreaMap(this);
    entities = new Entities(this);
    lua = LuaMapping.mapping(new LuaMappingWorld(this));
  }
  
  public boolean setArea(Area area) {
    if (area == null) throw new IllegalArgumentException("Null");
    return setArea(area.areaX, area.areaZ, area);
  }

  public boolean setArea(int areaX, int areaZ, Area area) {
    return map.setArea(areaX, areaZ, area);
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
    updateLock.writeLock();
    entities.lock.writeLock();
    Iterator<Entry<UUID, Entity>> iterator = entities.entrySet().iterator();
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
    entities.lock.writeUnlock();
    updateLock.writeUnlock();
  }

  public float getWorldSunlight() {
    updateLock.readLock();
    int t = time < MAX_TIME / 2 ? time : MAX_TIME - time;
    float f = ((float) t) / (((float) MAX_TIME) / 2f);
    updateLock.readUnlock();
    return f;
  }
  
  public boolean isDay() {
    updateLock.readLock();
    int time = this.time;
    updateLock.readUnlock();
    return time >= MAX_TIME / 4 && time < MAX_TIME * 3 / 4;
  }

  public void setTime(int time) {
    updateLock.writeLock();
    this.time = time % MAX_TIME;
    updateLock.writeUnlock();
  }
  
  public int getTime() {
    updateLock.readLock();
    return updateLock.readUnlock(time);
  }

  public TerrainGenerator getTerrainGenerator() {
    return terrainGenerator;
  }
  
  @Override
  public Lock getLock() {
    return updateLock;
  }
  
  @Override
  public void dispose() {
    if (!disposed.compareAndSet(false, true)) return;
    
    WorldTasks.waitSaveFinish();
    Lock.lockAll(true, this, map, entities);
    
    if (Side.isServer()|| !Area.isShared()) {
      for (Area area : map) {
        area.unload();
      }
    }
    map.empty();
    for (Entity entity : entities.values()) {
      entity.dispose();
    }
    entities.clear();
    
    map.lock.writeUnlock();
    entities.lock.writeUnlock();
    updateLock.writeUnlock();
  }
  
  public boolean isDisposed() {
    return disposed.get();
  }

  public Entity getEntity(UUID uuid) {
    entities.lock.readLock();
    return entities.lock.readUnlock(entities.get(uuid));
  }

  public void addEntity(Entity entity) {
    entities.lock.writeLock();
    entities.put(entity.uuid, entity);
    entities.lock.writeUnlock();
  }

  public void removeEntity(UUID uuid) {
    entities.lock.writeLock();
    Entity remove = entities.remove(uuid);
    if (remove != null) remove.dispose();
    entities.lock.writeUnlock();
  }

  public void updateEntity(DataGroup data) {
    entities.lock.writeLock();
    UUID uuid = (UUID) data.get("uuid");
    Entity entity = entities.get(uuid);
    if (entity != null) {
      entity.read(data);
    } else {
      Log.warning("No entity with uuid " + uuid.toString());
      addEntity(Entity.readType(data));
    }
    entities.lock.writeUnlock();
  }

  public void syncEntity(UUID uuid) {

  }
  
  public void addEntityFromSave(DataGroup dataGroup) {
    entities.lock.writeLock();
    Entity entity = Entity.readType(dataGroup);
    if (entity != null && !entities.containsKey(entity.uuid)) {
      addEntity(entity);
    }
    entities.lock.writeUnlock();
  }

  public void save() {

  }
}

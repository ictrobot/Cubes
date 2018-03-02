package ethanjones.cubes.world.server;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.performance.Performance;
import ethanjones.cubes.core.performance.PerformanceTags;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketEntityAdd;
import ethanjones.cubes.networking.packets.PacketEntityRemove;
import ethanjones.cubes.networking.packets.PacketEntityUpdate;
import ethanjones.cubes.networking.packets.PacketWorldTime;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.generator.RainStatus;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.GenerationTask;
import ethanjones.cubes.world.thread.WorldRequestParameter;
import ethanjones.cubes.world.thread.WorldTasks;
import ethanjones.data.DataGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class WorldServer extends World {

  private static List<LoadedAreaFilter> loadedAreaFilters = new CopyOnWriteArrayList<>();

  private RainStatus rainStatusOverride = null;
  private long rainStatusOverrideEnd = 0;

  public WorldServer(Save save) {
    super(save);
    if (save == null) throw new IllegalArgumentException("Null save on server");
    
    if (this.save.fileHandle != null) {
      Log.info("Save '" + this.save.name + "' in '" + this.save.fileHandle.file().getAbsolutePath() + "'");
    } else {
      Log.info("Save '" + this.save.name + "'");
    }

    loadedAreaFilters.add(WorldTasks.getGenerationAreaFilter());
  }
  
  public BlockReference getSpawnPoint() {
    return terrainGenerator.spawnPoint(this);
  }

  @Override
  public void tick() {
    Performance.start(PerformanceTags.SERVER_WORLD_UPDATE);
    updateLock.writeLock();
    super.tick();

    Performance.start(PerformanceTags.SERVER_WORLD_AREA_TICK);
    map.lock.readLock();
    for (Area area : map) {
      area.tick();
    }
    map.lock.readUnlock();
    Performance.stop(PerformanceTags.SERVER_WORLD_AREA_TICK);
  
    updateLock.writeUnlock();
    Performance.stop(PerformanceTags.SERVER_WORLD_UPDATE);
  }

  /**
   * Only call after saving areas
   */
  public void unloadDistantAreas(Iterable<Area> areas) {
    ArrayList<Area> removed = new ArrayList<Area>();
    AreaReference areaReference = new AreaReference();
    for (Area area : areas) {
      areaReference.setFromArea(area);
      if (!shouldAreaBeLoaded(areaReference)) removed.add(area);
    }

    map.lock.writeLock();
    int i = map.getSize();
    for (Area area : removed) {
      area.unload();
      map.setArea(area.areaX, area.areaZ, null);
    }
    map.lock.writeUnlock();
  }

  public boolean shouldAreaBeLoaded(AreaReference areaReference) {
    for (LoadedAreaFilter loadedAreaFilter : loadedAreaFilters) {
      if (loadedAreaFilter.load(areaReference)) return true;
    }
    return false;
  }

  public void addLoadedAreaFilter(LoadedAreaFilter filter) {
    loadedAreaFilters.add(filter);
  }

  public boolean removeLoadedAreaFilter(LoadedAreaFilter filter) {
    return loadedAreaFilters.remove(filter);
  }

  @Override
  public GenerationTask requestRegion(MultiAreaReference references, WorldRequestParameter parameter) {
    return WorldTasks.request(this, references, parameter);
  }

  @Override
  public void dispose() {
    super.dispose();
  }

  @Override
  public void addEntity(Entity entity) {
    entities.lock.writeLock();
    super.addEntity(entity);

    PacketEntityAdd packet = new PacketEntityAdd();
    packet.entity = entity;
    NetworkingManager.sendPacketToAllClients(packet);
    entities.lock.writeUnlock();
  }

  @Override
  public void removeEntity(UUID uuid) {
    entities.lock.writeLock();
    super.removeEntity(uuid);

    PacketEntityRemove packet = new PacketEntityRemove();
    packet.uuid = uuid;
    NetworkingManager.sendPacketToAllClients(packet);
    entities.lock.writeUnlock();
  }

  @Override
  public void updateEntity(DataGroup data) {
    entities.lock.writeLock();
    super.updateEntity(data);

    PacketEntityUpdate packet = new PacketEntityUpdate();
    packet.data = data;
    NetworkingManager.sendPacketToAllClients(packet);
    entities.lock.writeUnlock();
  }

  @Override
  public void syncEntity(UUID uuid) {
    Entity entity = getEntity(uuid);
    if (entity == null) return;
    for (ClientIdentifier clientIdentifier : Cubes.getServer().getAllClients()) {
      if (clientIdentifier.getPlayerManager().positionInLoadRange(entity.position)) {
        PacketEntityUpdate packet = new PacketEntityUpdate();
        packet.data = entity.write();
        NetworkingManager.sendPacketToClient(packet, clientIdentifier);
      }
    }
  }

  @Override
  public void setTime(int time) {
    super.setTime(time);
    NetworkingManager.sendPacketToAllClients(new PacketWorldTime(this.time));
  }

  @Override
  public void save() {
    if (save == null || save.readOnly) {
      map.lock.writeLock();
      unloadDistantAreas(map);
      map.lock.writeUnlock();
      return;
    }
    updateLock.readLock();

    // players
    save.writePlayers();
    // areas
    save.writeAreas(map);
    // state
    save.getSaveOptions().worldTime = time;
    save.writeSaveOptions();

    updateLock.readUnlock();
  }

  public RainStatus getRainStatus(float x, float z) {
    Log.info(String.valueOf(rainStatusOverride));
    if (rainStatusOverride != null) {
      if (playingTime > rainStatusOverrideEnd) {
        rainStatusOverride = null;
        rainStatusOverrideEnd = 0;
      } else {
        return rainStatusOverride;
      }
    }
    return terrainGenerator.getRainStatus(x, z, playingTime);
  }

  public void overrideRainStatus(RainStatus rainStatus, int seconds) {
    rainStatusOverride = rainStatus;
    rainStatusOverrideEnd = playingTime + (seconds * (1000 / Cubes.tickMS));
  }

  public void removeRainStatusOverride() {
    rainStatusOverride = null;
    rainStatusOverrideEnd = 0;
  }
}

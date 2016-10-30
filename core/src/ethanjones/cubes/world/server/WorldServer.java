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
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.thread.GenerationTask;
import ethanjones.cubes.world.thread.WorldRequestParameter;
import ethanjones.cubes.world.thread.WorldTasks;
import ethanjones.data.DataGroup;

import java.util.Map.Entry;
import java.util.UUID;

public class WorldServer extends World {

  public WorldServer(Save save) {
    super(save);
    if (save == null) throw new IllegalArgumentException("Null save on server");
    Log.info("Save '" + this.save.name + "' in '" + this.save.fileHandle.file().getAbsolutePath() + "'");
    spawnpoint.setFromBlockReference(terrainGenerator.spawnPoint(this));
  }

  @Override
  public void tick() {
    Performance.start(PerformanceTags.SERVER_WORLD_UPDATE);
    lock.writeLock();
    super.tick();

    Performance.start(PerformanceTags.SERVER_WORLD_AREA_TICK);
    for (Entry<AreaReference, Area> entry : map.entrySet()) {
      entry.getValue().tick();
    }
    Performance.stop(PerformanceTags.SERVER_WORLD_AREA_TICK);

    lock.writeUnlock();
    Performance.stop(PerformanceTags.SERVER_WORLD_UPDATE);
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
    lock.writeLock();
    super.addEntity(entity);

    PacketEntityAdd packet = new PacketEntityAdd();
    packet.entity = entity;
    NetworkingManager.sendPacketToAllClients(packet);
    lock.writeUnlock();
  }

  @Override
  public void removeEntity(UUID uuid) {
    lock.writeLock();
    super.removeEntity(uuid);

    PacketEntityRemove packet = new PacketEntityRemove();
    packet.uuid = uuid;
    NetworkingManager.sendPacketToAllClients(packet);
    lock.writeUnlock();
  }

  @Override
  public void updateEntity(DataGroup data) {
    lock.writeLock();
    super.updateEntity(data);

    PacketEntityUpdate packet = new PacketEntityUpdate();
    packet.data = data;
    NetworkingManager.sendPacketToAllClients(packet);
    lock.writeUnlock();
  }

  @Override
  public void syncEntity(UUID uuid) {
    Entity entity = getEntity(uuid);
    if (entity == null) return;
    PacketEntityUpdate packet = new PacketEntityUpdate();
    packet.data = entity.write();
    NetworkingManager.sendPacketToAllClients(packet);
  }

  @Override
  public void setTime(int time) {
    super.setTime(time);
    NetworkingManager.sendPacketToAllClients(new PacketWorldTime(this.time));
  }
}

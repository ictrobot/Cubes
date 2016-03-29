package ethanjones.cubes.world.server;

import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketEntityAdd;
import ethanjones.cubes.networking.packets.PacketEntityRemove;
import ethanjones.cubes.networking.packets.PacketEntityUpdate;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.thread.WorldTasks;
import ethanjones.data.DataGroup;

import java.util.UUID;

public class WorldServer extends World {

  public WorldServer(TerrainGenerator terrainGenerator) {
    super(terrainGenerator);
    spawnpoint.setFromBlockReference(terrainGenerator.spawnPoint(this));
  }

  @Override
  public void requestRegion(MultiAreaReference references) {
    WorldTasks.request(this, references);
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
}

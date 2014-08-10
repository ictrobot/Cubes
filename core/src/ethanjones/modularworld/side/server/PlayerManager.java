package ethanjones.modularworld.side.server;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.events.EventHandler;
import ethanjones.modularworld.core.events.world.block.BlockEvent;
import ethanjones.modularworld.core.thread.Threads;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.networking.packets.PacketBlockChanged;
import ethanjones.modularworld.networking.packets.PacketConnect;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.side.server.thread.SendWorldCallable;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.coordinates.Coordinates;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.thread.GenerateWorld;

public class PlayerManager {

  private PacketConnect packetConnect;
  private SocketMonitor socketMonitor;
  private Player player;
  private Coordinates playerCoordinates;

  private int renderDistance;

  public PlayerManager(PacketConnect packetConnect) {
    ModularWorldServer.instance.playerManagers.add(this);
    this.packetConnect = packetConnect;
    this.socketMonitor = packetConnect.getSocketMonitor();
    this.player = new Player(packetConnect.username); //TODO: Check if known
    this.playerCoordinates = new Coordinates(player.position);

    renderDistance = packetConnect.renderDistance;

    ModularWorld.eventBus.register(this);

    initialLoadAreas();
  }

  public void playerChangedPosition() {
    playerCoordinates = new Coordinates(player.position);
  }

  private void initialLoadAreas() {
    AreaReference check = new AreaReference();
    for (int areaX = playerCoordinates.areaX - renderDistance; areaX <= playerCoordinates.areaX + renderDistance; areaX++) {
      check.areaX = areaX;
      for (int areaY = Math.max(playerCoordinates.areaY - renderDistance, 0); areaY <= playerCoordinates.areaY + renderDistance; areaY++) {
        check.areaY = areaY;
        for (int areaZ = playerCoordinates.areaZ - renderDistance; areaZ <= playerCoordinates.areaZ + renderDistance; areaZ++) {
          check.areaZ = areaZ;
          sendAndRequestArea(check);
        }
      }
    }
  }

  @EventHandler
  public void blockChanged(BlockEvent blockEvent) {
    BlockCoordinates coordinates = blockEvent.getBlockCoordinates();
    if (Math.abs(coordinates.areaX - playerCoordinates.areaX) > renderDistance) return;
    if (Math.abs(coordinates.areaY - playerCoordinates.areaY) > renderDistance) return;
    if (Math.abs(coordinates.areaZ - playerCoordinates.areaZ) > renderDistance) return;
    PacketBlockChanged packet = new PacketBlockChanged();
    packet.x = coordinates.blockX;
    packet.y = coordinates.blockY;
    packet.z = coordinates.blockZ;
    BlockFactory factory = ModularWorldServer.instance.world.getBlockFactory(packet.x, packet.y, packet.z);
    if (factory == null) return;
    packet.factory = ModularWorld.blockManager.toInt(factory);
    socketMonitor.getSocketOutput().getPacketQueue().addPacket(packet);
  }

  private void sendAndRequestArea(AreaReference areaReference) {
    if (ModularWorldServer.instance.world.getAreaInternal(areaReference, false, false) == null) {
      requestArea(areaReference.clone());
    } else {
      Threads.execute(new SendWorldCallable(ModularWorldServer.instance.world.getAreaInternal(areaReference, false, false), socketMonitor.getSocketOutput().getPacketQueue()));
    }
  }

  private void requestArea(AreaReference areaReference) {
    Threads.execute(new SendWorldCallable(new GenerateWorld(areaReference, (ethanjones.modularworld.world.WorldServer) ModularWorldServer.instance.world), socketMonitor.getSocketOutput().getPacketQueue()));
  }
}

package ethanjones.modularworld.side.server;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.events.EventHandler;
import ethanjones.modularworld.core.events.world.block.BlockEvent;
import ethanjones.modularworld.core.thread.Threads;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.networking.packets.PacketBlockChanged;
import ethanjones.modularworld.networking.packets.PacketConnect;
import ethanjones.modularworld.networking.packets.PacketConnected;
import ethanjones.modularworld.networking.packets.PacketPlayerInfo;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.coordinates.Coordinates;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.BlankArea;
import ethanjones.modularworld.world.thread.GenerateWorldCallable;
import ethanjones.modularworld.world.thread.SendWorldCallable;

public class PlayerManager {

  private PacketConnect packetConnect;
  private SocketMonitor socketMonitor;
  private Player player;
  private volatile Coordinates playerCoordinates; //any methods that access should be 'synchronized'

  private int renderDistance;

  public PlayerManager(PacketConnect packetConnect) {
    ModularWorldServer.instance.playerManagers.put(packetConnect.getSocketMonitor(), this);
    this.packetConnect = packetConnect;
    this.socketMonitor = packetConnect.getSocketMonitor();
    this.player = new Player(packetConnect.username); //TODO Check if known
    this.playerCoordinates = new Coordinates(player.position);

    renderDistance = packetConnect.renderDistance;

    ModularWorld.eventBus.register(this);

    socketMonitor.queue(new PacketConnected());

    initialLoadAreas();
  }

  public void playerChangedPosition() {
    playerCoordinates = new Coordinates(player.position);
  }

  public void handleInfo(PacketPlayerInfo packetPlayerInfo) {
    AreaReference n = new AreaReference().setFromVector3(packetPlayerInfo.position);
    AreaReference o = new AreaReference().setFromVector3(player.position);
    if (!n.equals(o)) {
      AreaReference check = new AreaReference();
      for (int areaX = n.areaX - renderDistance; areaX <= n.areaX + renderDistance; areaX++) {
        check.areaX = areaX;
        for (int areaY = Math.max(n.areaY - renderDistance, 0); areaY <= n.areaY + renderDistance; areaY++) {
          check.areaY = areaY;
          for (int areaZ = n.areaZ - renderDistance; areaZ <= n.areaZ + renderDistance; areaZ++) {
            check.areaZ = areaZ;
            if (Math.abs(areaX - o.areaX) > renderDistance || Math.abs(areaY - o.areaY) > renderDistance || Math.abs(areaZ - o.areaZ) > renderDistance) {
              sendAndRequestArea(check);
            }
          }
        }
      }
    }
    if (!player.position.equals(packetPlayerInfo.position)) {
      player.position = packetPlayerInfo.position;
      playerChangedPosition();
    }
    player.angle = packetPlayerInfo.angle;
  }


  private synchronized void initialLoadAreas() {
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
  public synchronized void blockChanged(BlockEvent blockEvent) {
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
    Area area = ModularWorldServer.instance.world.getAreaInternal(areaReference, false, false);
    if (area == null || area instanceof BlankArea) {
      requestArea(areaReference.clone());
    } else {
      Threads.execute(new SendWorldCallable(ModularWorldServer.instance.world.getAreaInternal(areaReference, false, false), socketMonitor.getSocketOutput().getPacketQueue(), this));
    }
  }

  private void requestArea(AreaReference areaReference) {
    Threads.execute(new SendWorldCallable(new GenerateWorldCallable(areaReference, (ethanjones.modularworld.world.WorldServer) ModularWorldServer.instance.world), socketMonitor.getSocketOutput().getPacketQueue(), this));
  }

  public void sendPacket(Packet packet) {
    socketMonitor.queue(packet);
  }

  public synchronized boolean shouldSendArea(int areaX, int areaY, int areaZ) {
    if (Math.abs(areaX - playerCoordinates.areaX) <= renderDistance && Math.abs(areaY - playerCoordinates.areaY) <= renderDistance && Math.abs(areaZ - playerCoordinates.areaZ) <= renderDistance) {
      return true;
    }
    return false;
  }
}

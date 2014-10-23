package ethanjones.modularworld.side.server;

import com.badlogic.gdx.math.collision.Ray;
import ethanjones.modularworld.block.Blocks;
import ethanjones.modularworld.core.events.EventHandler;
import ethanjones.modularworld.core.events.world.block.BlockEvent;
import ethanjones.modularworld.core.system.Threads;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.graphics.world.RayTracing;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.networking.packets.*;
import ethanjones.modularworld.side.Sided;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;
import ethanjones.modularworld.world.coordinates.Coordinates;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.reference.BlockReference;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.BlankArea;
import ethanjones.modularworld.world.thread.GenerateWorldCallable;
import ethanjones.modularworld.world.thread.SendWorldCallable;

public class PlayerManager {

  private PacketConnect packetConnect;
  private SocketMonitor socketMonitor;
  private Player player;
  private volatile Coordinates playerCoordinates; //blocks should be "synchronized (playerCoordinates) {"

  private int renderDistance;

  public PlayerManager(PacketConnect packetConnect) {
    ModularWorldServer.instance.playerManagers.put(packetConnect.getSocketMonitor(), this);
    this.packetConnect = packetConnect;
    this.socketMonitor = packetConnect.getSocketMonitor();
    this.player = new Player(packetConnect.username); //TODO Check if known
    this.playerCoordinates = new Coordinates(player.position);

    renderDistance = packetConnect.renderDistance;

    Sided.getEventBus().register(this);

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
      player.position.set(packetPlayerInfo.position);
      playerChangedPosition();
    }
    player.angle.set(packetPlayerInfo.angle);
  }


  private void initialLoadAreas() {
    AreaReference check = new AreaReference();
    synchronized (playerCoordinates) {
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
  }

  @EventHandler
  public void blockChanged(BlockEvent blockEvent) {
    BlockCoordinates coordinates = blockEvent.getBlockCoordinates();
    synchronized (playerCoordinates) {
      if (Math.abs(coordinates.areaX - playerCoordinates.areaX) > renderDistance) return;
      if (Math.abs(coordinates.areaY - playerCoordinates.areaY) > renderDistance) return;
      if (Math.abs(coordinates.areaZ - playerCoordinates.areaZ) > renderDistance) return;
    }
    PacketBlockChanged packet = new PacketBlockChanged();
    packet.x = coordinates.blockX;
    packet.y = coordinates.blockY;
    packet.z = coordinates.blockZ;
    packet.factory = Sided.getBlockManager().toInt(ModularWorldServer.instance.world.getBlockFactory(packet.x, packet.y, packet.z));
    socketMonitor.getSocketOutput().getPacketQueue().addPacket(packet);
  }

  private void sendAndRequestArea(AreaReference areaReference) {
    Area area = ModularWorldServer.instance.world.getAreaInternal(areaReference, false, false);
    if (area == null || area instanceof BlankArea) {
      Threads.execute(new SendWorldCallable(new GenerateWorldCallable(areaReference.clone(), (ethanjones.modularworld.world.WorldServer) ModularWorldServer.instance.world), socketMonitor.getSocketOutput().getPacketQueue(), this));
    } else {
      Threads.execute(new SendWorldCallable(ModularWorldServer.instance.world.getAreaInternal(areaReference, false, false), socketMonitor.getSocketOutput().getPacketQueue(), this));
    }
  }

  public void sendPacket(Packet packet) {
    socketMonitor.queue(packet);
  }

  public boolean shouldSendArea(int areaX, int areaY, int areaZ) {
    synchronized (playerCoordinates) {
      if (Math.abs(areaX - playerCoordinates.areaX) <= renderDistance && Math.abs(areaY - playerCoordinates.areaY) <= renderDistance && Math.abs(areaZ - playerCoordinates.areaZ) <= renderDistance) {
        return true;
      }
      return false;
    }
  }

  public void click(PacketClick.Click type) {
    if (type == PacketClick.Click.left) {
      BlockReference blockReference = RayTracing.getIntersection(new Ray(player.position, player.angle), ModularWorldServer.instance.world, 8);
      if (blockReference == null) return;
      ModularWorldServer.instance.world.setBlockFactory(Blocks.stone, blockReference.blockX, blockReference.blockY, blockReference.blockZ);
    }
  }
}

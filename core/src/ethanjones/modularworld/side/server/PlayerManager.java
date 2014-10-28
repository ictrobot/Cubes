package ethanjones.modularworld.side.server;

import ethanjones.modularworld.core.events.EventHandler;
import ethanjones.modularworld.core.events.world.block.BlockEvent;
import ethanjones.modularworld.core.system.Threads;
import ethanjones.modularworld.core.util.MathHelper;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.graphics.world.RayTracing;
import ethanjones.modularworld.networking.packet.Packet;
import ethanjones.modularworld.networking.packets.*;
import ethanjones.modularworld.networking.socket.SocketMonitor;
import ethanjones.modularworld.side.Sided;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.reference.BlockReference;
import ethanjones.modularworld.world.storage.Area;
import ethanjones.modularworld.world.storage.BlankArea;
import ethanjones.modularworld.world.thread.GenerateWorldCallable;
import ethanjones.modularworld.world.thread.SendWorldCallable;

public class PlayerManager {

  private final Player player;
  private final AreaReference playerArea;
  private PacketConnect packetConnect;
  private SocketMonitor socketMonitor;
  private int renderDistance;

  public PlayerManager(PacketConnect packetConnect) {
    this.packetConnect = packetConnect;
    this.socketMonitor = packetConnect.getPacketEnvironment().getReceiving().getSocketMonitor();
    this.player = new Player(packetConnect.username); //TODO Store users and world
    this.playerArea = new AreaReference().setFromPositionVector3(player.position);

    ModularWorldServer.instance.playerManagers.put(socketMonitor, this);

    renderDistance = packetConnect.renderDistance;

    Sided.getEventBus().register(this);

    socketMonitor.queue(new PacketConnected());

    initialLoadAreas();
  }

  public void handleInfo(PacketPlayerInfo packetPlayerInfo) {
    AreaReference n = new AreaReference().setFromPositionVector3(packetPlayerInfo.position);
    AreaReference o = new AreaReference().setFromPositionVector3(player.position);
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
      synchronized (playerArea) {
        playerArea.setFromPositionVector3(player.position);
      }
    }
    player.angle.set(packetPlayerInfo.angle);
  }


  private void initialLoadAreas() {
    AreaReference check = new AreaReference();
    synchronized (playerArea) {
      for (int areaX = playerArea.areaX - renderDistance; areaX <= playerArea.areaX + renderDistance; areaX++) {
        check.areaX = areaX;
        for (int areaY = Math.max(playerArea.areaY - renderDistance, 0); areaY <= playerArea.areaY + renderDistance; areaY++) {
          check.areaY = areaY;
          for (int areaZ = playerArea.areaZ - renderDistance; areaZ <= playerArea.areaZ + renderDistance; areaZ++) {
            check.areaZ = areaZ;
            sendAndRequestArea(check);
          }
        }
      }
    }
  }

  @EventHandler
  public void blockChanged(BlockEvent blockEvent) {
    BlockReference blockReference = blockEvent.getBlockReference();
    synchronized (playerArea) {
      if (Math.abs(MathHelper.area(blockReference.blockX) - playerArea.areaX) > renderDistance) return;
      if (Math.abs(MathHelper.area(blockReference.blockY) - playerArea.areaY) > renderDistance) return;
      if (Math.abs(MathHelper.area(blockReference.blockZ) - playerArea.areaZ) > renderDistance) return;
    }
    PacketBlockChanged packet = new PacketBlockChanged();
    packet.x = blockReference.blockX;
    packet.y = blockReference.blockY;
    packet.z = blockReference.blockZ;
    packet.block = Sided.getBlockManager().toInt(ModularWorldServer.instance.world.getBlock(packet.x, packet.y, packet.z));
    socketMonitor.queue(packet);
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
    synchronized (playerArea) {
      if (Math.abs(areaX - playerArea.areaX) <= renderDistance && Math.abs(areaY - playerArea.areaY) <= renderDistance && Math.abs(areaZ - playerArea.areaZ) <= renderDistance) {
        return true;
      }
      return false;
    }
  }

  public void click(PacketClick.Click type) {
    if (type == PacketClick.Click.left) {
      RayTracing.BlockIntersection blockIntersection = RayTracing.getBlockIntersection(player.position, player.angle, ModularWorldServer.instance.world);
      if (blockIntersection == null) return;
      BlockReference blockReference = blockIntersection.getBlockReference();
      ModularWorldServer.instance.world.setBlock(null, blockReference.blockX, blockReference.blockY, blockReference.blockZ);
    }
  }
}

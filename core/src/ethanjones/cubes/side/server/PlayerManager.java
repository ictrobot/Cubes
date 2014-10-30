package ethanjones.cubes.side.server;

import ethanjones.cubes.core.events.EventHandler;
import ethanjones.cubes.core.events.world.block.BlockEvent;
import ethanjones.cubes.core.system.Threads;
import ethanjones.cubes.core.util.MathHelper;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.world.RayTracing;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packets.*;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.storage.Area;
import ethanjones.cubes.world.storage.BlankArea;
import ethanjones.cubes.world.thread.GenerateWorldCallable;
import ethanjones.cubes.world.thread.SendWorldCallable;

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

    CubesServer.instance.playerManagers.put(socketMonitor, this);

    renderDistance = packetConnect.renderDistance;

    Sided.getEventBus().register(this);

    socketMonitor.queue(new PacketConnected());

    initialLoadAreas();
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

  private void sendAndRequestArea(AreaReference areaReference) {
    Area area = CubesServer.instance.world.getAreaInternal(areaReference, false, false);
    if (area == null || area instanceof BlankArea) {
      Threads.execute(new SendWorldCallable(new GenerateWorldCallable(areaReference.clone(), (ethanjones.cubes.world.WorldServer) CubesServer.instance.world), socketMonitor.getSocketOutput().getPacketQueue(), this));
    } else {
      Threads.execute(new SendWorldCallable(CubesServer.instance.world.getAreaInternal(areaReference, false, false), socketMonitor.getSocketOutput().getPacketQueue(), this));
    }
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
    packet.block = Sided.getBlockManager().toInt(CubesServer.instance.world.getBlock(packet.x, packet.y, packet.z));
    socketMonitor.queue(packet);
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
      RayTracing.BlockIntersection blockIntersection = RayTracing.getBlockIntersection(player.position, player.angle, CubesServer.instance.world);
      if (blockIntersection == null) return;
      BlockReference blockReference = blockIntersection.getBlockReference();
      CubesServer.instance.world.setBlock(null, blockReference.blockX, blockReference.blockY, blockReference.blockZ);
    }
  }
}

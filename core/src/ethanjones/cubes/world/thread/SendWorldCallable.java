package ethanjones.cubes.world.thread;

import java.util.concurrent.Callable;

import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.networking.packets.PacketArea;
import ethanjones.cubes.side.server.PlayerManager;
import ethanjones.cubes.world.storage.Area;

public class SendWorldCallable implements Callable {

  private final GenerateWorldCallable generateWorldCallable;
  private final Area area;
  private final PacketQueue packetQueue;
  private final PlayerManager playerManager;

  public SendWorldCallable(Area area, PacketQueue packetQueue) {
    this.generateWorldCallable = null;
    this.area = area;
    this.packetQueue = packetQueue;
    this.playerManager = null;
  }

  public SendWorldCallable(Area area, PacketQueue packetQueue, PlayerManager playerManager) {
    this.generateWorldCallable = null;
    this.area = area;
    this.packetQueue = packetQueue;
    this.playerManager = playerManager;
  }

  public SendWorldCallable(GenerateWorldCallable generateWorldCallable, PacketQueue packetQueue) {
    this.generateWorldCallable = generateWorldCallable;
    this.area = null;
    this.packetQueue = packetQueue;
    this.playerManager = null;
  }

  public SendWorldCallable(GenerateWorldCallable generateWorldCallable, PacketQueue packetQueue, PlayerManager playerManager) {
    this.generateWorldCallable = generateWorldCallable;
    this.area = null;
    this.packetQueue = packetQueue;
    this.playerManager = playerManager;
  }

  @Override
  public Object call() throws Exception {
    Area area = generateWorldCallable != null ? generateWorldCallable.call() : this.area;
    PacketArea packetArea = new PacketArea();
    packetArea.areaX = area.x;
    packetArea.areaY = area.y;
    packetArea.areaZ = area.z;
    packetArea.playerManager = playerManager;
    packetArea.area = area.write();
    packetQueue.addPacket(packetArea);
    return area;
  }
}

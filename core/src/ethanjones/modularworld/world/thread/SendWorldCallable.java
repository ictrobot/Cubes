package ethanjones.modularworld.world.thread;

import ethanjones.modularworld.networking.common.packet.PacketQueue;
import ethanjones.modularworld.networking.packets.PacketArea;
import ethanjones.modularworld.world.storage.Area;

import java.util.concurrent.Callable;

public class SendWorldCallable implements Callable {

  private final GenerateWorldCallable generateWorldCallable;
  private final Area area;
  private final PacketQueue packetQueue;

  public SendWorldCallable(Area area, PacketQueue packetQueue) {
    this.generateWorldCallable = null;
    this.area = area;
    this.packetQueue = packetQueue;
  }

  public SendWorldCallable(GenerateWorldCallable generateWorldCallable, PacketQueue packetQueue) {
    this.generateWorldCallable = generateWorldCallable;
    this.area = null;
    this.packetQueue = packetQueue;
  }

  @Override
  public Object call() throws Exception {
    Area area = generateWorldCallable != null ? generateWorldCallable.call() : this.area;
    PacketArea packetArea = new PacketArea();
    packetArea.areaX = area.x;
    packetArea.areaY = area.y;
    packetArea.areaZ = area.z;
    packetArea.area = area.write();
    packetQueue.addPacket(packetArea);
    return area;
  }
}

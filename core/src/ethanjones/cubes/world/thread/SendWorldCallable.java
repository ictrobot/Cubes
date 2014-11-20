package ethanjones.cubes.world.thread;

import java.util.concurrent.Callable;

import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketArea;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.server.PlayerManager;
import ethanjones.cubes.world.storage.Area;

public class SendWorldCallable implements Callable {

  private final ClientIdentifier clientIdentifier;
  private final GenerateWorldCallable generateWorldCallable;
  private final Area area;
  private final PlayerManager playerManager;

  public SendWorldCallable(Area area, ClientIdentifier clientIdentifier, PlayerManager playerManager) {
    this.clientIdentifier = clientIdentifier;
    this.generateWorldCallable = null;
    this.area = area;
    this.playerManager = playerManager;
  }

  public SendWorldCallable(GenerateWorldCallable generateWorldCallable, ClientIdentifier clientIdentifier, PlayerManager playerManager) {
    this.clientIdentifier = clientIdentifier;
    this.generateWorldCallable = generateWorldCallable;
    this.area = null;
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
    NetworkingManager.sendPacketToClient(packetArea, clientIdentifier);
    return area;
  }
}

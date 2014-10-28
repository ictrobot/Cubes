package ethanjones.modularworld.networking.packets;

import ethanjones.data.DataGroup;
import ethanjones.modularworld.networking.packet.DataPacket;
import ethanjones.modularworld.networking.packet.environment.PacketPriority;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.PlayerManager;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

public class PacketArea extends DataPacket {

  public static AreaReference areaReference = new AreaReference();
  public int areaX;
  public int areaY;
  public int areaZ;
  public DataGroup area;
  public PlayerManager playerManager;

  public PacketArea() {
    getPacketEnvironment().getSending().setPacketPriority(PacketPriority.Low);
  }

  @Override
  public void handlePacket() {
    if (getPacketEnvironment().getReceiving().getSide() != Side.Client) return;
    areaReference.setFromAreaCoordinates(areaX, areaY, areaZ);
    Area a = new Area(areaX, areaY, areaZ);
    a.read(area);
    ModularWorldClient.instance.world.setAreaInternal(areaReference, a);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setInteger("areaX", areaX);
    dataGroup.setInteger("areaY", areaY);
    dataGroup.setInteger("areaZ", areaZ);
    dataGroup.setGroup("area", area);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    areaX = dataGroup.getInteger("areaX");
    areaY = dataGroup.getInteger("areaY");
    areaZ = dataGroup.getInteger("areaZ");
    area = dataGroup.getGroup("area");
  }

  @Override
  public boolean shouldSend() {
    if (playerManager == null) return true;
    return playerManager.shouldSendArea(areaX, areaY, areaZ);
  }
}

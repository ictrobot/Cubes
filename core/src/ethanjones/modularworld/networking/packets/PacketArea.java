package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.packet.PacketPriority;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.PlayerManager;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

public class PacketArea extends Packet {

  public static AreaReference areaReference = new AreaReference();
  public int areaX;
  public int areaY;
  public int areaZ;
  public DataGroup area;
  public PlayerManager playerManager;

  public PacketArea() {
    super(PacketPriority.LOW);
  }

  @Override
  public void handlePacket() {
    if (getSide() != Side.Client) return;
    areaReference.setFromArea(areaX, areaY, areaZ);
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

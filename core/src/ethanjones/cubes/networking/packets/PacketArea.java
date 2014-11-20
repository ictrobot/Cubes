package ethanjones.cubes.networking.packets;

import ethanjones.data.DataGroup;

import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.PlayerManager;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

public class PacketArea extends DataPacket {

  public static AreaReference areaReference = new AreaReference();
  public int areaX;
  public int areaY;
  public int areaZ;
  public DataGroup area;
  public PlayerManager playerManager;

  public PacketArea() {
    setPacketPriority(PacketPriority.Low);
  }

  @Override
  public void handlePacket() {
    if ( Sided.getSide() != Side.Client) return;
    areaReference.setFromAreaCoordinates(areaX, areaY, areaZ);
    Area a = new Area(areaX, areaY, areaZ);
    a.read(area);
    Cubes.getClient().world.setAreaInternal(areaReference, a);
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

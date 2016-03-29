package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Sided;
import ethanjones.data.DataGroup;

public class PacketConnected extends DataPacket {

  public DataGroup idManager;

  public PacketConnected() {
    setPacketPriority(PacketPriority.High);
  }

  @Override
  public void handlePacket() {
    Sided.getIDManager().read(idManager);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("idManager", idManager);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    idManager = dataGroup.getGroup("idManager");
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

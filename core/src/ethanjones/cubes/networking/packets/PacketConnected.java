package ethanjones.cubes.networking.packets;

import ethanjones.data.DataGroup;

import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Sided;

public class PacketConnected extends DataPacket {

  public PacketConnected() {
    setPacketPriority(PacketPriority.High);
  }

  @Override
  public void handlePacket() {

  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setGroup("blockManager", Sided.getBlockManager().write());
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    Sided.getBlockManager().read(dataGroup.getGroup("blockManager"));
  }
}

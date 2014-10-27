package ethanjones.modularworld.networking.packets;

import ethanjones.data.DataGroup;
import ethanjones.modularworld.networking.packet.DataPacket;
import ethanjones.modularworld.networking.packet.environment.PacketPriority;
import ethanjones.modularworld.side.Sided;

public class PacketConnected extends DataPacket {

  public PacketConnected() {
    getPacketEnvironment().getSending().setPacketPriority(PacketPriority.High);
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

  @Override
  public void handlePacket() {

  }
}

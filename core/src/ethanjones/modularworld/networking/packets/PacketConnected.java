package ethanjones.modularworld.networking.packets;

import ethanjones.data.DataGroup;
import ethanjones.modularworld.networking.common.packet.DataPacket;
import ethanjones.modularworld.side.Sided;

public class PacketConnected extends DataPacket {

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

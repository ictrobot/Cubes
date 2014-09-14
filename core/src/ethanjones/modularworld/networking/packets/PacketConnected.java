package ethanjones.modularworld.networking.packets;

import ethanjones.data.DataGroup;
import ethanjones.modularworld.networking.common.packet.DataPacket;
import ethanjones.modularworld.side.common.ModularWorld;

public class PacketConnected extends DataPacket {

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setGroup("blockManager", ModularWorld.blockManager.write());
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    ModularWorld.blockManager.read(dataGroup.getGroup("blockManager"));
  }

  @Override
  public void handlePacket() {

  }
}

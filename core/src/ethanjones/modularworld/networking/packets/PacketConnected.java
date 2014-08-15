package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.side.common.ModularWorld;

public class PacketConnected extends Packet {

  public int renderDistance;
  public String username;

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

package ethanjones.cubes.common.networking.packets;

import ethanjones.data.DataGroup;

import ethanjones.cubes.common.networking.packet.DataPacket;
import ethanjones.cubes.common.networking.packet.PacketPriority;
import ethanjones.cubes.common.Sided;

public class PacketConnected extends DataPacket {

  public DataGroup blockManager;

  public PacketConnected() {
    setPacketPriority(PacketPriority.High);
  }

  @Override
  public void handlePacket() {
    Sided.getBlockManager().read(blockManager);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setGroup("blockManager", blockManager);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    blockManager = dataGroup.getGroup("blockManager");
  }

}

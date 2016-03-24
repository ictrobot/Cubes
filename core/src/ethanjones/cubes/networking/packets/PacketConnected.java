package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Sided;
import ethanjones.data.DataGroup;

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
    dataGroup.put("blockManager", blockManager);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    blockManager = dataGroup.getGroup("blockManager");
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

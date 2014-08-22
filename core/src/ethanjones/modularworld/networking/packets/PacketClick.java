package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.packet.PacketPriority;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class PacketClick extends Packet {

  public static enum Click {
    left, middle, right;

    public static Click get(int button) {
      switch (button) {
        case 0:
          return left;
        case 1:
          return right;
        case 2:
          return middle;
      }
      return null;
    }
  }

  public PacketClick() {
    super(PacketPriority.HIGH);
  }

  public Click type;

  @Override
  public void handlePacket() {
    ModularWorldServer.instance.playerManagers.get(this.getSocketMonitor()).click(type);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setInteger("type", type.ordinal());
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    type = Click.values()[data.getInteger("type")];
  }
}

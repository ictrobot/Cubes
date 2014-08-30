package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.packet.PacketPriority;
import ethanjones.modularworld.side.server.ModularWorldServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PacketClick extends Packet {

  public Click type;

  public PacketClick() {
    super(PacketPriority.HIGH);
  }

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeInt(type.ordinal());
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    type = Click.values()[dataInputStream.readInt()];
  }

  @Override
  public void handlePacket() {
    ModularWorldServer.instance.playerManagers.get(this.getSocketMonitor()).click(type);
  }

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
}

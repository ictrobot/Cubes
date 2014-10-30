package ethanjones.cubes.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.server.CubesServer;

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

  public Click type;

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
    CubesServer.instance.playerManagers.get(getPacketEnvironment().getReceiving().getSocketMonitor()).click(type);
  }
}

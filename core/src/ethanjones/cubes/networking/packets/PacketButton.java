package ethanjones.cubes.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.server.CubesServer;

public class PacketButton extends Packet {

  public static final int BUTTON_DOWN = 0;
  public static final int BUTTON_UP = 1;

  public int button;
  public int action;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeInt(button);
    dataOutputStream.writeInt(action);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    button = dataInputStream.readInt();
    action = dataInputStream.readInt();
  }

  @Override
  public void handlePacket() {
    CubesServer.instance.playerManagers.get(getPacketEnvironment().getReceiving().getSocketMonitor()).handlePacket(this);
  }
}

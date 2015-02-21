package ethanjones.cubes.common.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.common.networking.packet.Packet;
import ethanjones.cubes.Cubes;

public class PacketKey extends Packet {

  public static final int KEY_DOWN = 0;
  public static final int KEY_UP = 1;

  public int key;
  public int action;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeInt(key);
    dataOutputStream.writeInt(action);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    key = dataInputStream.readInt();
    action = dataInputStream.readInt();
  }

  @Override
  public void handlePacket() {
    Cubes.getServer().getClient(getSocketMonitor()).getPlayerManager().handlePacket(this);
  }
}

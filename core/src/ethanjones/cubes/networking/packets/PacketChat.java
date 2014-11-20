package ethanjones.cubes.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public class PacketChat extends Packet {

  public String msg;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeUTF(msg);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    msg = dataInputStream.readUTF();
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Server) {
      Log.info(" [Chat]" + msg);
      NetworkingManager.sendPacketToAllClients(this);
    }
  }
}

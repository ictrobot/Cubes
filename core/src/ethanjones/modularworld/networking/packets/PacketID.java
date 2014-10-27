package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.networking.packet.Packet;
import ethanjones.modularworld.networking.packet.environment.PacketPriority;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PacketID extends Packet {

  public String c;
  public int id;

  public PacketID() {
    getPacketEnvironment().getSending().setPacketPriority(PacketPriority.High);
  }

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeUTF(c);
    dataOutputStream.writeInt(id);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    c = dataInputStream.readUTF();
    id = dataInputStream.readInt();
    getPacketEnvironment().getReceiving().getSocketMonitor().getNetworking().getPacketIDDatabase().process(this);
  }

  @Override
  public void handlePacket() {
    //Should process here...
  }
}

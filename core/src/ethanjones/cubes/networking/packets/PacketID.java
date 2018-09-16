package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.OMNIDIRECTIONAL)
@Priority(PacketPriority.CONNECTION_INITIALIZATION)
public class PacketID extends Packet {

  public String c;
  public int id;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeUTF(c);
    dataOutputStream.writeInt(id);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    c = dataInputStream.readUTF();
    id = dataInputStream.readInt();
    getSocketMonitor().getPacketIDDatabase().process(this);
  }

  @Override
  public void handlePacket() {
    //Should process here...
  }

  @Override
  public String toString() {
    return super.toString() + " " + c + " " + id;
  }
}

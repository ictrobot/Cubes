package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.networking.socket.SocketMonitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_SERVER)
@Priority(PacketPriority.CONNECTION_INITIALIZATION)
public class PacketConnectedReply extends Packet {

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {

  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {

  }

  @Override
  public void handlePacket() {
    SocketMonitor socketMonitor = getSocketMonitor();
    if (socketMonitor != null) socketMonitor.getSocketOutput().setConnectionInitialized();
  }
}

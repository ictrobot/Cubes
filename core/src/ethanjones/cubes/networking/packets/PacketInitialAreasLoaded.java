package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.TO_CLIENT)
@Priority(PacketPriority.LOW)
public class PacketInitialAreasLoaded extends Packet {

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {

  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {

  }

  @Override
  public void handlePacket() {
    Cubes.getClient().worldReady = true;
  }

  @Override
  public Packet copy() {
    return new PacketInitialAreasLoaded();
  }
}

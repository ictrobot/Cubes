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
public class PacketInitialAreasProgress extends Packet {
  public float progress;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeFloat(progress);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    progress = dataInputStream.readFloat();
  }

  @Override
  public void handlePacket() {
    if (progress > Cubes.getClient().worldProgress) Cubes.getClient().worldProgress = progress;
  }
}

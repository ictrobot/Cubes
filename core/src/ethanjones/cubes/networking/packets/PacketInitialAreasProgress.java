package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_CLIENT)
public class PacketInitialAreasProgress extends Packet {
  public float progress;

  public PacketInitialAreasProgress() {
    setPacketPriority(PacketPriority.Low);
  }

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeFloat(progress);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    progress = dataInputStream.readFloat();
  }

  @Override
  public void handlePacket() {
    if (progress > Cubes.getClient().worldProgress) Cubes.getClient().worldProgress = progress;
  }
}

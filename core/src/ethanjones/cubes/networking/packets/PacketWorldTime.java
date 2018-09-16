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
@Priority(PacketPriority.HIGH)
public class PacketWorldTime extends Packet {

  public PacketWorldTime() {

  }

  public PacketWorldTime(int time) {
    this.time = time;
  }

  public int time;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeInt(time);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    time = dataInputStream.readInt();
  }

  @Override
  public void handlePacket() {
    Cubes.getClient().world.setTime(time);
  }

  @Override
  public String toString() {
    return super.toString() + " " + time;
  }
}

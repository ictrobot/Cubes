package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_SERVER)
public class PacketWorldTime extends Packet {

  public PacketWorldTime() {
    setPacketPriority(PacketPriority.High);
  }

  public PacketWorldTime(int time) {
    setPacketPriority(PacketPriority.High);
    this.time = time;
  }

  public int time;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeInt(time);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
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

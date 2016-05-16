package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_CLIENT)
public class PacketInitialAreasLoaded extends Packet {

  public PacketInitialAreasLoaded() {
    setPacketPriority(PacketPriority.Low);
  }

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {

  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {

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

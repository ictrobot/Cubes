package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

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
    if (Sided.getSide() == Side.Client) Cubes.getClient().ready = true;
  }

  @Override
  public Packet copy() {
    return new PacketInitialAreasLoaded();
  }
}

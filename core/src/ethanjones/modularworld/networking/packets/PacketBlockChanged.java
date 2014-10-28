package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.networking.packet.Packet;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.reference.AreaReference;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PacketBlockChanged extends Packet {

  public int x;
  public int y;
  public int z;
  public int block;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeInt(x);
    dataOutputStream.writeInt(y);
    dataOutputStream.writeInt(z);
    dataOutputStream.writeInt(block);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    x = dataInputStream.readInt();
    y = dataInputStream.readInt();
    z = dataInputStream.readInt();
    block = dataInputStream.readInt();
  }

  @Override
  public void handlePacket() {
    ModularWorldClient.instance.world.getArea(new AreaReference().setFromBlockCoordinates(x, y, z)).handleChange(this);
  }
}

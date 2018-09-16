package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.TO_CLIENT)
public class PacketBlockChanged extends Packet {

  public int x;
  public int y;
  public int z;
  public int block;
  public int meta;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeInt(x);
    dataOutputStream.writeInt(y);
    dataOutputStream.writeInt(z);
    dataOutputStream.writeInt(block);
    dataOutputStream.writeInt(meta);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    x = dataInputStream.readInt();
    y = dataInputStream.readInt();
    z = dataInputStream.readInt();
    block = dataInputStream.readInt();
    meta = dataInputStream.readInt();
  }

  @Override
  public void handlePacket() {
    Cubes.getClient().world.setBlock(IDManager.toBlock(block), x, y, z);
  }

  @Override
  public String toString() {
    return super.toString() + " " + x + "," + y + "," + z + " " + block;
  }
}

package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.TO_CLIENT)
public class PacketPlayerNoClip extends Packet {

  public boolean enabled = false;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeBoolean(enabled);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    enabled = dataInputStream.readBoolean();
  }

  @Override
  public void handlePacket() {
    Cubes.getClient().player.setNoClip(enabled);
  }
}

package ethanjones.cubes.networking.packets;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class PacketOtherPlayerConnected extends Packet {
  //TODO PacketPlayerDisconnected ???
  public String username;
  public UUID uuid;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeUTF(username);
    dataOutputStream.writeLong(uuid.getMostSignificantBits());
    dataOutputStream.writeLong(uuid.getLeastSignificantBits());
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    username = dataInputStream.readUTF();
    uuid = new UUID(dataInputStream.readLong(), dataInputStream.readLong());
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Client) {
      new Player(username, uuid).addToWorld();
    }
  }

  @Override
  public String toString() {
    return super.toString() + " " + uuid.toString() + " " + username;
  }
}

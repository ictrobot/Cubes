package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.math.Vector3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

public class PacketOtherPlayerMovement extends Packet {

  public PacketOtherPlayerMovement() {
  }

  public PacketOtherPlayerMovement(Player player) {
    this.angle = player.angle.cpy();
    this.position = player.position.cpy();
    this.uuid = player.uuid;
  }

  public Vector3 angle;
  public Vector3 position;
  public UUID uuid;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    VectorUtil.stream(angle, dataOutputStream);
    VectorUtil.stream(position, dataOutputStream);
    dataOutputStream.writeLong(uuid.getMostSignificantBits());
    dataOutputStream.writeLong(uuid.getLeastSignificantBits());
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    angle = VectorUtil.stream(dataInputStream);
    position = VectorUtil.stream(dataInputStream);
    uuid = new UUID(dataInputStream.readLong(), dataInputStream.readLong());
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Client) {
      Entity entity = Cubes.getClient().world.getEntity(uuid);
      if (entity instanceof Player) {
        entity.angle.set(angle);
        entity.position.set(position);
      } else {
        Log.warning("No player with uuid " + uuid.toString());
      }
    }
  }

  @Override
  public String toString() {
    return super.toString() + " " + uuid.toString() + " " + position.toString() + " " + angle.toString();
  }
}

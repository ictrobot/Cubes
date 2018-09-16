package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.math.Vector3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.OMNIDIRECTIONAL)
@Priority(PacketPriority.HIGH)
public class PacketPlayerMovement extends Packet {

  public PacketPlayerMovement() {

  }

  public PacketPlayerMovement(Player player) {
    this.angle = player.angle.cpy();
    this.position = player.position.cpy();
  }

  public Vector3 angle;
  public Vector3 position;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    VectorUtil.stream(angle, dataOutputStream);
    VectorUtil.stream(position, dataOutputStream);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    angle = VectorUtil.stream(dataInputStream);
    position = VectorUtil.stream(dataInputStream);
  }

  @Override
  public void handlePacket() {
    if (Side.isServer()) {
      Cubes.getServer().getClient(getSocketMonitor()).getPlayerManager().handlePacket(this);
    } else {
      Cubes.getClient().player.angle.set(angle);
      Cubes.getClient().player.position.set(position);
    }
  }

  @Override
  public String toString() {
    return super.toString() + " " + position.toString() + " " + angle.toString();
  }
}

package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.math.Vector3;

import java.io.DataInputStream;
import java.io.DataOutputStream;

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
  public void write(DataOutputStream dataOutputStream) throws Exception {
    VectorUtil.stream(angle, dataOutputStream);
    VectorUtil.stream(position, dataOutputStream);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    angle = VectorUtil.stream(dataInputStream);
    position = VectorUtil.stream(dataInputStream);
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Server) {
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

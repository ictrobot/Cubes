package ethanjones.cubes.networking.packets;

import com.badlogic.gdx.math.Vector3;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

public class PacketPlayerInfo extends Packet {

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
}

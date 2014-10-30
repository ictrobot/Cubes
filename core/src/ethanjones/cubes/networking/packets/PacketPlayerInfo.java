package ethanjones.cubes.networking.packets;

import com.badlogic.gdx.math.Vector3;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;

public class PacketPlayerInfo extends Packet {

  public Vector3 angle;
  public Vector3 position;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    VectorUtil.writeVector3(angle, dataOutputStream);
    VectorUtil.writeVector3(position, dataOutputStream);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    angle = VectorUtil.readVector3(dataInputStream);
    position = VectorUtil.readVector3(dataInputStream);
  }

  @Override
  public void handlePacket() {
    if (getPacketEnvironment().getReceiving().getSide() == Side.Server) {
      CubesServer.instance.playerManagers.get(getPacketEnvironment().getReceiving().getSocketMonitor()).handleInfo(this);
    } else {
      CubesClient.instance.player.angle.set(angle);
      CubesClient.instance.player.position.set(position);
    }
  }
}

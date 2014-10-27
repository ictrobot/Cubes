package ethanjones.modularworld.networking.packets;

import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.core.util.VectorUtil;
import ethanjones.modularworld.networking.packet.Packet;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.ModularWorldServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;

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
      ModularWorldServer.instance.playerManagers.get(getPacketEnvironment().getReceiving().getSocketMonitor()).handleInfo(this);
    } else {
      ModularWorldClient.instance.player.angle.set(angle);
      ModularWorldClient.instance.player.position.set(position);
    }
  }
}

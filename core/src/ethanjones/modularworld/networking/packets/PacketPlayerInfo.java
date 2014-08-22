package ethanjones.modularworld.networking.packets;

import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.util.VectorUtil;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class PacketPlayerInfo extends Packet {

  public Vector3 angle;
  public Vector3 position;

  @Override
  public void handlePacket() {
    if (getSide() == Side.Server) {
      ModularWorldServer.instance.playerManagers.get(getSocketMonitor()).handleInfo(this);
    } else {
      ModularWorldClient.instance.player.angle.set(angle);
      ModularWorldClient.instance.player.position.set(position);
    }
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setList("angle", VectorUtil.dataFromVector3(angle));
    dataGroup.setList("position", VectorUtil.dataFromVector3(position));
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    position = VectorUtil.vector3FromData(data.getList("position"));
    angle = VectorUtil.vector3FromData(data.getList("angle"));
  }
}

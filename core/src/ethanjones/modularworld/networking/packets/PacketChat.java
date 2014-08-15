package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.server.ModularWorldServer;
import ethanjones.modularworld.side.server.PlayerManager;

public class PacketChat extends Packet {

  public String msg;

  @Override
  public void handlePacket() {
    if (getSide() == Side.Server) {
      Log.info("Chat", msg);
      for (PlayerManager playerManager : ModularWorldServer.instance.playerManagers) {
        playerManager.sendPacket(this);
      }
    }
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setString("msg", msg);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    msg = data.getString("msg");
  }
}

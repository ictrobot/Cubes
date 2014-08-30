package ethanjones.modularworld.networking.packets;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.server.ModularWorldServer;
import ethanjones.modularworld.side.server.PlayerManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PacketChat extends Packet {

  public String msg;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeUTF(msg);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    msg = dataInputStream.readUTF();
  }

  @Override
  public void handlePacket() {
    if (getSide() == Side.Server) {
      Log.info("Chat", msg);
      for (PlayerManager playerManager : ModularWorldServer.instance.playerManagers.values()) {
        playerManager.sendPacket(this);
      }
    }
  }
}

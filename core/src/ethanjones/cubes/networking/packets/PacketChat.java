package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.command.CommandManager;

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
    if (Sided.getSide() == Side.Server) {
      Player player = Cubes.getServer().getClient(getSocketMonitor()).getPlayer();
      Log.info("[" + Localization.get("server.chat") + "] [" + player.username + "] " + msg);
      if (msg.startsWith("/")) {
        CommandManager.run(msg.substring(1), player);
      } else {
        PacketChat packetChat = new PacketChat();
        packetChat.msg = "[" + player.username + "] " + msg;
        NetworkingManager.sendPacketToAllClients(packetChat);
      }
    } else {
      Cubes.getClient().renderer.guiRenderer.print(msg);
    }
  }

  @Override
  public String toString() {
    return super.toString() + " " + msg;
  }
}

package ethanjones.cubes.common.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.common.core.localization.Localization;
import ethanjones.cubes.common.core.logging.Log;
import ethanjones.cubes.common.entity.living.player.Player;
import ethanjones.cubes.common.networking.NetworkingManager;
import ethanjones.cubes.common.networking.packet.Packet;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.common.Sided;
import ethanjones.cubes.Cubes;
import ethanjones.cubes.server.command.CommandManager;

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
}

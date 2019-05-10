package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.command.CommandManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.OMNIDIRECTIONAL)
public class PacketChat extends Packet {

  public String msg;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeUTF(msg);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    msg = dataInputStream.readUTF();
  }

  @Override
  public void handlePacket() {
    if (Side.isServer()) {
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
      Log.info("[" + Localization.get("client.chat") + "] " + msg);
      Cubes.getClient().renderer.guiRenderer.newMessage(msg);
    }
  }

  @Override
  public String toString() {
    return super.toString() + " " + msg;
  }
}

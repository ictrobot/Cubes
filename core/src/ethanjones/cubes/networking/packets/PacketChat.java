package ethanjones.cubes.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.environment.SendingPacketEnvironment;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.side.server.PlayerManager;

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
    if (getPacketEnvironment().getReceiving().getSide() == Side.Server) {
      Log.info(" [Chat]" + msg);
      setPacketEnvironment(new SendingPacketEnvironment());
      for (PlayerManager playerManager : CubesServer.instance.playerManagers.values()) {
        playerManager.sendPacket(this);
      }
    }
  }
}

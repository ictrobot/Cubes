package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.PlayerManager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.TO_SERVER)
@Priority(PacketPriority.CONNECTION_INITIALIZATION)
public class PacketPingConfirm extends Packet {
  public long serverTime = 0;
  public long serverReceiveTime = System.nanoTime();
  
  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeLong(serverTime);
  }
  
  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    serverTime = dataInputStream.readLong();
  }
  
  @Override
  public void handlePacket() {
    long serverPing = serverReceiveTime - serverTime;
    if (serverPing < 0) Log.error("Negative ping time! " + serverPing);
    PlayerManager manager = Cubes.getServer().getClient(getSocketMonitor()).getPlayerManager();
    manager.lastPingNano = serverReceiveTime;
    if (manager.connectionPing < 0) {
      manager.connectionPing = serverPing;
    } else {
      manager.connectionPing = (manager.connectionPing * 0.6d) + (serverPing * 0.4d);
    }
    if (Networking.NETWORKING_DEBUG)
      Log.debug(getSocketMonitor().remoteAddress + " Ping: " + manager.connectionPing + " current: " + serverPing);
  }
}

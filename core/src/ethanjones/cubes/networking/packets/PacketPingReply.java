package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.client.ClientNetworking;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.side.common.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_CLIENT)
@Priority(PacketPriority.HIGH)
public class PacketPingReply extends Packet {
  public long clientTime = 0;
  public long clientReceiveTime = System.nanoTime();
  public long serverTime = System.nanoTime();
  
  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeLong(clientTime);
    dataOutputStream.writeLong(serverTime);
  }
  
  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    clientTime = dataInputStream.readLong();
    serverTime = dataInputStream.readLong();
    
    PacketPingConfirm ppc = new PacketPingConfirm();
    ppc.serverTime = serverTime;
    getSocketMonitor().getSocketOutput().getPacketQueue().add(ppc);
  }
  
  @Override
  public void handlePacket() {
    long clientPing = clientReceiveTime - clientTime;
    if (clientPing < 0) Log.error("Negative ping time! " + clientPing);
    ClientNetworking networking = (ClientNetworking) NetworkingManager.getNetworking(Side.Client);
    networking.awaitingPingResponse = false;
    if (networking.ping < 0) {
      networking.ping = clientPing;
    } else {
      networking.ping = (networking.ping * 0.6d) + (clientPing * 0.4d);
    }
    if (Networking.NETWORKING_DEBUG) Log.debug("Ping: " + networking.ping + " current: " + clientPing);
  }
}

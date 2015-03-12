package ethanjones.cubes.networking.singleplayer;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public class SingleplayerNetworking extends Networking {

  PacketQueue toServer;
  PacketQueue toClient;
  
  public SingleplayerNetworking() {
    toServer = new PacketQueue();
    toClient = new PacketQueue();
  }

  @Override
  public void preInit() throws Exception {
    setNetworkingState(NetworkingState.Starting);
  }

  @Override
  public void init() {
    setNetworkingState(NetworkingState.Running);
  }

  @Override
  public void update() {

  }

  @Override
  public void stop() {
    setNetworkingState(NetworkingState.Stopping);
  }

  @Override
  public void sendPacketToServer(Packet packet) throws UnsupportedOperationException {
    send(packet, toServer);
  }

  @Override
  public void sendPacketToClient(Packet packet, ClientIdentifier clientIdentifier) throws UnsupportedOperationException {
    send(packet, toClient);
  }

  private void send(Packet packet, PacketQueue packetQueue) {
    Executor.execute(new IOClonePacketRunnable(packet, packetQueue));
  }

  @Override
  public void disconnected(SocketMonitor socketMonitor, Exception e) {
    Log.warning("Method disconnected(SocketMonitor, Exception) should not be called in Singleplayer");
  }

  @Override
  public void processPackets() {
    Side side = Sided.getSide();
    PacketQueue packetQueue;
    switch (side) {
      case Client:
        packetQueue = toClient;
        break;
      case Server:
        packetQueue = toServer;
        break;
      default:
        return;
    }
    Packet packet = null;
    while ((packet = packetQueue.get()) != null) {
      packet.handlePacket();
    }
  }
}

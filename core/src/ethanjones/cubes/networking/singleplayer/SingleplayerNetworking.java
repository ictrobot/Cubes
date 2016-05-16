package ethanjones.cubes.networking.singleplayer;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public class SingleplayerNetworking extends Networking {

  PacketQueue toServer;
  PacketQueue toClient;
  SingleplayerNetworkingThread cloneToServer;
  SingleplayerNetworkingThread cloneToClient;
  
  public SingleplayerNetworking() {
    toServer = new PacketQueue(false);
    toClient = new PacketQueue(false);
    cloneToServer = new SingleplayerNetworkingThread(toServer, Side.Server);
    cloneToClient = new SingleplayerNetworkingThread(toClient, Side.Client);
  }

  @Override
  public void preInit() throws Exception {
    setNetworkingState(NetworkingState.Starting);
    cloneToServer.start();
    cloneToClient.start();
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
    cloneToClient.running.set(false);
    cloneToServer.running.set(false);
    setNetworkingState(NetworkingState.Stopping);
  }

  @Override
  public void sendPacketToServer(Packet packet) throws UnsupportedOperationException {
    PacketDirection.checkPacketSend(packet.getClass(), Side.Client);
    send(packet, cloneToServer);
  }

  @Override
  public void sendPacketToClient(Packet packet, ClientIdentifier clientIdentifier) throws UnsupportedOperationException {
    PacketDirection.checkPacketSend(packet.getClass(), Side.Server);
    send(packet, cloneToClient);
  }

  private void send(Packet packet, SingleplayerNetworkingThread cloneThread) {
    cloneThread.input.add(packet);
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
      PacketDirection.checkPacketReceive(packet.getClass(), side);
      packet.handlePacket();
    }
  }
}

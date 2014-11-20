package ethanjones.cubes.networking.singleplayer;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketBuffer;
import ethanjones.cubes.networking.packet.PacketIDDatabase;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;

public class SingleplayerNetworking extends Networking {

  PacketBuffer toServer;
  PacketBuffer toClient;
  
  public SingleplayerNetworking() {
    toServer = new PacketBuffer();
    toClient = new PacketBuffer();
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
    toServer.addPacket(packet);
  }

  @Override
  public void sendPacketToClient(Packet packet, ClientIdentifier clientIdentifier) throws UnsupportedOperationException {
    toClient.addPacket(packet);
  }

  @Override
  public void disconnected(SocketMonitor socketMonitor, Exception e) {
    Log.warning("Method disconnected(SocketMonitor, Exception) should not be called in Singleplayer");
  }

  @Override
  public void received(SocketMonitor socketMonitor, Packet packet) {
    Log.warning("Method received(SocketMonitor, Packet) should not be called in Singleplayer");
  }

  @Override
  public void processPackets() {
    Side side = Sided.getSide();
    if (side == null) return;
    switch (side) {
      case Client:
        toClient.process();
        break;
      case Server:
        toServer.process();
        break;
    }
  }
}

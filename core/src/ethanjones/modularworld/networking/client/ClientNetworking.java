package ethanjones.modularworld.networking.client;

import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.common.Networking;
import ethanjones.modularworld.networking.common.NetworkingState;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.networking.packets.PacketPlayerInfo;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.ModularWorldClient;

public class ClientNetworking extends Networking {

  private final String host;
  private final int port;
  private SocketMonitor socketMonitor;

  public ClientNetworking(String host, int port) {
    super(Side.Client);
    this.host = host;
    this.port = port;
  }

  public synchronized void start() {
    setNetworkingState(NetworkingState.Starting);
    Log.info("Starting Client Networking");
    socketMonitor = new SocketMonitor(Gdx.net.newClientSocket(NetworkingManager.protocol, host, port, NetworkingManager.socketHints), this);
    setNetworkingState(NetworkingState.Running);
    Log.info("Successfully connected to " + host + ":" + port);
  }

  @Override
  public synchronized void update() {
    if (getNetworkingState() != NetworkingState.Running) GraphicalAdapter.instance.gotoMainMenu();
    PacketPlayerInfo packetPlayerInfo = new PacketPlayerInfo();
    packetPlayerInfo.angle = ModularWorldClient.instance.player.angle;
    packetPlayerInfo.position = ModularWorldClient.instance.player.position;
    sendToServer(packetPlayerInfo);
  }

  @Override
  public synchronized void stop() {
    setNetworkingState(NetworkingState.Stopping);
    Log.info("Stopping Client Networking");
    socketMonitor.dispose();
    setNetworkingState(NetworkingState.Stopped);
  }

  public synchronized void sendToServer(Packet packet) {
    socketMonitor.queue(packet);
  }

  @Override
  public synchronized void disconnected(SocketMonitor socketMonitor, Exception e) {
    if (getNetworkingState() != NetworkingState.Stopping && getNetworkingState() != NetworkingState.Stopped) {
      Log.info("Disconnected from " + socketMonitor.getRemoteAddress(), e);
      stop();
    }
  }
}

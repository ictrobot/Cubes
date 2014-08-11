package ethanjones.modularworld.networking.client;

import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.NetworkUtil;
import ethanjones.modularworld.networking.common.Networking;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.side.Side;

public class ClientNetworking extends Networking {

  private final String host;
  private final int port;
  private SocketMonitor socketMonitor;

  public ClientNetworking(String host, int port) {
    super(Side.Client);
    this.host = host;
    this.port = port;
  }

  public void start() {
    Log.info("Starting Client Networking");
    socketMonitor = new SocketMonitor(Gdx.net.newClientSocket(NetworkUtil.protocol, host, port, NetworkUtil.socketHints), this);
    Log.info("Successfully connected to " + host + ":" + port);
  }

  @Override
  public void stop() {
    Log.info("Stopping Client Networking");
    socketMonitor.dispose();
  }

  public void sendToServer(Packet packet) {
    socketMonitor.queue(packet);
  }

  @Override
  public void disconnected(SocketMonitor socketMonitor, Exception e) {
    Log.info("Disconnected from " + socketMonitor.getRemoteAddress());
    //TODO: Go back to main menu
  }
}

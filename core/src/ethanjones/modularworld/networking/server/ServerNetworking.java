package ethanjones.modularworld.networking.server;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.Networking;
import ethanjones.modularworld.networking.common.NetworkingState;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.side.Side;

public class ServerNetworking extends Networking {

  private final int port;
  private Array<SocketMonitor> sockets;
  private SocketMonitorServer serverSocketMonitor;

  public ServerNetworking(int port) {
    super(Side.Server);
    sockets = new Array<SocketMonitor>();
    this.port = port;
    serverSocketMonitor = new SocketMonitorServer(port);
  }

  public synchronized void start() {
    setNetworkingState(NetworkingState.Starting);
    Log.info("Starting Server Networking");
    serverSocketMonitor.start();
    setNetworkingState(NetworkingState.Running);
  }

  @Override
  public synchronized void update() {

  }

  @Override
  public synchronized void stop() {
    Log.info("Stopping Server Networking");
    serverSocketMonitor.dispose();
    for (int i = 0; i < sockets.size; i++) {
      sockets.pop().dispose();
    }
  }

  protected synchronized void accepted(Socket socket) {
    sockets.add(new SocketMonitor(socket, this));
    Log.info("Successfully connected to " + socket.getRemoteAddress());
  }

  @Override
  public synchronized void disconnected(SocketMonitor socketMonitor, Exception e) {
    if (getNetworkingState() != NetworkingState.Stopping && getNetworkingState() != NetworkingState.Stopped) {
      Log.info("Disconnected from " + socketMonitor.getRemoteAddress(), e);
      stop();
    }
  }
}

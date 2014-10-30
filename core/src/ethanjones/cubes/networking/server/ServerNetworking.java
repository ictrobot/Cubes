package ethanjones.cubes.networking.server;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.server.CubesServer;

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
  public synchronized void tick() {

  }

  @Override
  public synchronized void stop() {
    if (getNetworkingState() != NetworkingState.Running) return;
    setNetworkingState(NetworkingState.Stopping);
    Log.info("Stopping Server Networking");
    serverSocketMonitor.dispose();
    for (int i = 0; i < sockets.size; i++) {
      sockets.pop().dispose();
    }
    setNetworkingState(NetworkingState.Stopped);
  }

  @Override
  public synchronized void disconnected(SocketMonitor socketMonitor, Exception e) {
    if (getNetworkingState() == NetworkingState.Stopping || getNetworkingState() == NetworkingState.Stopped) return;
    if (NetworkingManager.clientNetworking != null && (NetworkingManager.clientNetworking.getNetworkingState() == NetworkingState.Stopping || NetworkingManager.clientNetworking.getNetworkingState() == NetworkingState.Stopped)) {
      return;
    }
    Log.info("Disconnected from " + socketMonitor.getRemoteAddress(), e);
    CubesServer.instance.playerManagers.remove(socketMonitor);
  }

  protected synchronized void accepted(Socket socket) {
    sockets.add(new SocketMonitor(socket, this));
    Log.info("Successfully connected to " + socket.getRemoteAddress());
  }
}

package ethanjones.modularworld.networking.server;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.Networking;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.side.Side;

public class ServerNetworking extends Networking {

  private final int port;
  private Array<SocketMonitor> sockets;
  private SocketMonitorServer serverSocketMonitor;

  public ServerNetworking(int port) {
    super(Side.Server);
    this.port = port;
  }

  public void start() {
    Log.info("Starting Server Networking");
    sockets = new Array<SocketMonitor>();
    serverSocketMonitor = new SocketMonitorServer(port);
    serverSocketMonitor.start();
  }

  @Override
  public void stop() {
    Log.info("Stopping Server Networking");
    serverSocketMonitor.dispose();
    for (SocketMonitor socketMonitor : sockets) {
      socketMonitor.dispose();
    }
  }

  protected synchronized void accepted(Socket socket) {
    sockets.add(new SocketMonitor(socket, this));
    Log.info("Successfully connected to " + socket.getRemoteAddress());
  }

  @Override
  public void disconnected(SocketMonitor socketMonitor, Exception e) {
    Log.info("Disconnected from " + socketMonitor.getRemoteAddress());
    sockets.removeValue(socketMonitor, true);
  }
}

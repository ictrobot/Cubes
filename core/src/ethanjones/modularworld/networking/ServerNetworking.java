package ethanjones.modularworld.networking;

import com.badlogic.gdx.net.Socket;
import ethanjones.modularworld.core.logging.Log;

import java.util.HashMap;

public class ServerNetworking extends Networking {

  private HashMap<Socket, SocketMonitor> sockets;
  private SocketMonitorServer serverSocketMonitor;

  public ServerNetworking(int port) {
    super(port);
  }

  public void start() {
    Log.info("Starting Server Networking");
    sockets = new HashMap<Socket, SocketMonitor>();
    serverSocketMonitor = new SocketMonitorServer(port);
    serverSocketMonitor.start();
  }

  @Override
  public void stop() {
    Log.info("Stopping Server Networking");
    serverSocketMonitor.dispose();
    for (SocketMonitor socketMonitor : sockets.values()) {
      socketMonitor.dispose();
    }
  }

  protected synchronized void accepted(Socket socket) {
    SocketMonitor socketMonitor = new SocketMonitor(socket);
    socketMonitor.start();
    sockets.put(socket, socketMonitor);
  }
}

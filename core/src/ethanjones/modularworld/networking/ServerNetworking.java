package ethanjones.modularworld.networking;

import com.badlogic.gdx.net.Socket;
import ethanjones.modularworld.core.logging.Log;

import java.util.HashMap;

public class ServerNetworking extends Networking {

  public static HashMap<Socket, SocketMonitor> sockets;
  private static ServerSocketMonitor serverSocketMonitor;
  private static Thread threadServerSocketMonitor;

  public void start() {
    Log.debug("Starting Server Networking");
    sockets = new HashMap<Socket, SocketMonitor>();
    serverSocketMonitor = new ServerSocketMonitor();
    threadServerSocketMonitor = new Thread(serverSocketMonitor);
    threadServerSocketMonitor.setName(ServerSocketMonitor.class.getSimpleName());
    threadServerSocketMonitor.start();
  }

  protected synchronized void accepted(Socket socket) {
    SocketMonitor socketMonitor = new SocketMonitor(socket);
    Thread thread = new Thread(socketMonitor);
    thread.setName("Socket Monitor: " + socket.getRemoteAddress());
    thread.start();
    sockets.put(socket, socketMonitor);
  }

}

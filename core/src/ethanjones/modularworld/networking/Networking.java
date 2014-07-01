package ethanjones.modularworld.networking;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.core.logging.Log;

public class Networking {

  public static Array<Socket> sockets;
  public static int mainPort = 8080;
  private static ServerSocketMonitor serverSocketMonitor;
  private static Thread threadServerSocketMonitor;

  protected static synchronized void accepted(Socket socket) {
    sockets.add(socket);
  }

  public static void startServerNetworking() {
    Log.debug("Starting ServerNetworkMonitor");
    serverSocketMonitor = new ServerSocketMonitor();
    threadServerSocketMonitor = new Thread(serverSocketMonitor);
    threadServerSocketMonitor.setName(ServerSocketMonitor.class.getSimpleName());
    threadServerSocketMonitor.start();
  }

}

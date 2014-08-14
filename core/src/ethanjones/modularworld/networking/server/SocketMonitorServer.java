package ethanjones.modularworld.networking.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.NetworkingManager;

public class SocketMonitorServer implements Runnable, Disposable {

  public boolean running;
  private ServerSocket serverSocket;
  private Thread thread;
  private int port;

  public SocketMonitorServer(int port) {
    this.port = port;
    serverSocket = Gdx.net.newServerSocket(NetworkingManager.protocol, port, NetworkingManager.serverSocketHints);
    running = true;
  }

  @Override
  public void run() {
    while (running) {
      try {
        NetworkingManager.serverNetworking.accepted(serverSocket.accept(NetworkingManager.socketHints));
      } catch (Exception e) {
        if (running) Log.error(e);
      }
    }
    dispose();
  }

  public Thread start() {
    if (thread != null) return thread;
    thread = new Thread(this);
    thread.setDaemon(true);
    thread.setName("Server Socket Monitor: " + port);
    thread.start();
    return thread;
  }

  protected Thread getThread() {
    return thread;
  }

  @Override
  public void dispose() {
    running = false;
    serverSocket.dispose();
    getThread().interrupt();
  }
}

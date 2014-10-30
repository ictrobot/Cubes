package ethanjones.cubes.networking.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.utils.Disposable;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.NetworkingManager;

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

  @Override
  public void dispose() {
    running = false;
    serverSocket.dispose();
    getThread().interrupt();
  }

  protected Thread getThread() {
    return thread;
  }

  public Thread start() {
    if (thread != null) return thread;
    thread = new Thread(this);
    thread.setDaemon(true);
    thread.setName("Server Socket Monitor: " + port);
    thread.start();
    return thread;
  }
}

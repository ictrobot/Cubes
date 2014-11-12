package ethanjones.cubes.networking.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Disposable;
import java.io.DataOutputStream;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.networking.NetworkingManager;

public class ServerSocketMonitor implements Runnable, Disposable {

  public boolean running;
  private ServerSocket serverSocket;
  private Thread thread;
  private int port;

  public ServerSocketMonitor(int port) {
    this.port = port;
    serverSocket = Gdx.net.newServerSocket(NetworkingManager.protocol, port, NetworkingManager.serverSocketHints);
    running = true;
  }

  @Override
  public void run() {
    while (running) {
      try {
        Socket accept = serverSocket.accept(NetworkingManager.socketHints);
        DataOutputStream dataOutputStream = new DataOutputStream(accept.getOutputStream());
        dataOutputStream.writeInt(Branding.VERSION_MAJOR);
        dataOutputStream.writeInt(Branding.VERSION_MINOR);
        dataOutputStream.writeInt(Branding.VERSION_POINT);
        dataOutputStream.writeInt(Branding.VERSION_BUILD);
        dataOutputStream.writeUTF(Branding.VERSION_HASH);
        NetworkingManager.serverNetworking.accepted(accept);
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

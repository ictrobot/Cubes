package ethanjones.modularworld.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.net.ServerSocket;
import ethanjones.modularworld.ModularWorld;

public class ServerSocketMonitor implements Runnable {

  public boolean running;

  ServerSocket serverSocket;

  public ServerSocketMonitor() {
    running = true;
    serverSocket = Gdx.net.newServerSocket(Networking.protocol, Networking.mainPort, Networking.serverSocketHints);
  }

  @Override
  public void run() {
    while (running) {
      ((ServerNetworking) ModularWorld.instance.networking).accepted(serverSocket.accept(Networking.socketHints));
    }
  }
}

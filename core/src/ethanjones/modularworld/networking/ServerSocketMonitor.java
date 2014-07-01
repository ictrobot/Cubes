package ethanjones.modularworld.networking;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocket;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.SocketHints;

public class ServerSocketMonitor implements Runnable {

  public boolean running;

  ServerSocket serverSocket;
  ServerSocketHints serverSocketHints;
  SocketHints socketHints;

  public ServerSocketMonitor() {
    running = true;
    serverSocketHints = new ServerSocketHints();
    serverSocketHints.acceptTimeout = 0;
    socketHints = new SocketHints();
    socketHints.keepAlive = true;
    socketHints.connectTimeout = 0;
    serverSocket = Gdx.net.newServerSocket(Net.Protocol.TCP, Networking.mainPort, new ServerSocketHints());
  }

  @Override
  public void run() {
    while (running) {
      Networking.accepted(serverSocket.accept(socketHints));
    }
  }
}

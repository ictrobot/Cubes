package ethanjones.modularworld.networking.server;

import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.Networking;
import ethanjones.modularworld.networking.common.packet.Packet;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;

public class ServerNetworking extends Networking {

  private Array<SocketMonitor> sockets;
  private SocketMonitorServer serverSocketMonitor;

  public ServerNetworking(int port) {
    super(port);
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
  public void received(Packet packet, SocketMonitor socketMonitor) {

  }

  @Override
  public void disconnected(SocketMonitor socketMonitor, Exception e) {
    super.disconnected(socketMonitor, e);
    sockets.removeValue(socketMonitor, true);
  }
}

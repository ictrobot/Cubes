package ethanjones.modularworld.networking;

import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.packet.Packet;

public class ClientNetworking extends Networking {

  private final String host;
  private final int port;
  SocketMonitor socketMonitor;

  public ClientNetworking(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public void start() {
    Log.debug("Starting Client Networking");
    socketMonitor = new SocketMonitor(Gdx.net.newClientSocket(protocol, host, port, socketHints));
  }

  public synchronized void sendToServer(Packet packet) {
    send(packet.getPacketData(), socketMonitor);
  }

}

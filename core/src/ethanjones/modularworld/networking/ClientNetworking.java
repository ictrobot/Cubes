package ethanjones.modularworld.networking;

import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.packet.Packet;

public class ClientNetworking extends Networking {

  private final String host;
  private SocketMonitor socketMonitor;

  public ClientNetworking(String host, int port) {
    super(port);
    this.host = host;
  }

  public void start() {
    Log.info("Starting Client Networking");
    socketMonitor = new SocketMonitor(Gdx.net.newClientSocket(NetworkUtil.protocol, host, port, NetworkUtil.socketHints));
    socketMonitor.start();
  }

  @Override
  public void stop() {
    Log.info("Stopping Client Networking");
    socketMonitor.dispose();
  }

  public synchronized void sendToServer(Packet packet) {
    send(packet.getPacketData(), socketMonitor);
  }

}

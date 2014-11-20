package ethanjones.cubes.networking;

import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.SocketHints;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketIDDatabase;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;

public abstract class Networking {

  public static enum NetworkingState {
    Starting, Running, Stopping
  }

  public final static ServerSocketHints serverSocketHints;
  public final static SocketHints socketHints;

  static {
    serverSocketHints = new ServerSocketHints();
    serverSocketHints.acceptTimeout = 0;
    socketHints = new SocketHints();
    socketHints.keepAlive = true;
    socketHints.connectTimeout = 30000;
  }

  private volatile NetworkingState networkingState;
  private PacketIDDatabase packetIDDatabase;

  public Networking() {
    this.packetIDDatabase = new PacketIDDatabase();
  }

  public NetworkingState getNetworkingState() {
    return networkingState;
  }

  protected void setNetworkingState(NetworkingState networkingState) {
    this.networkingState = networkingState;
  }

  public abstract void preInit() throws Exception;

  public abstract void init();

  public abstract void update();

  public abstract void stop();

  public void sendPacketToServer(Packet packet) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  public void sendPacketToClient(Packet packet, ClientIdentifier clientIdentifier) throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }

  public abstract void disconnected(SocketMonitor socketMonitor, Exception e);

  public abstract void received(SocketMonitor socketMonitor, Packet packet);

  public abstract void processPackets();

}

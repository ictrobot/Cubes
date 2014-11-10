package ethanjones.cubes.networking.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packets.PacketButton;
import ethanjones.cubes.networking.packets.PacketPlayerInfo;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;

public class ClientNetworking extends Networking {

  public static int currentID = -1;
  private final String host;
  private final int port;
  private SocketMonitor socketMonitor;

  private Vector3 prevPosition = new Vector3();
  private Vector3 prevDirection = new Vector3();

  public ClientNetworking(String host, int port) {
    super(Side.Client);
    this.host = host;
    this.port = port;
  }

  public synchronized void start() {
    setNetworkingState(NetworkingState.Starting);
    Log.info("Starting Client Networking");
    socketMonitor = new SocketMonitor(Gdx.net.newClientSocket(NetworkingManager.protocol, host, port, NetworkingManager.socketHints), this);
    setNetworkingState(NetworkingState.Running);
    Log.info("Successfully connected to " + host + ":" + port);
  }

  @Override
  public synchronized void tick() {
    if (getNetworkingState() != NetworkingState.Running) Cubes.quit(false);

    if (!CubesClient.instance.player.position.equals(prevPosition) || !CubesClient.instance.player.angle.equals(prevDirection)) {
      PacketPlayerInfo packetPlayerInfo = new PacketPlayerInfo();
      packetPlayerInfo.angle = CubesClient.instance.player.angle;
      packetPlayerInfo.position = CubesClient.instance.player.position;
      sendToServer(packetPlayerInfo);
      prevPosition.set(CubesClient.instance.player.position);
      prevDirection.set(CubesClient.instance.player.angle);
    }
  }

  @Override
  public synchronized void stop() {
    if (getNetworkingState() != NetworkingState.Running) return;
    setNetworkingState(NetworkingState.Stopping);
    Log.info("Stopping Client Networking");
    socketMonitor.dispose();
    setNetworkingState(NetworkingState.Stopped);
  }

  @Override
  public synchronized void disconnected(SocketMonitor socketMonitor, Exception e) {
    if (getNetworkingState() == NetworkingState.Stopping || getNetworkingState() == NetworkingState.Stopped) return;
    if (NetworkingManager.serverNetworking != null && (NetworkingManager.serverNetworking.getNetworkingState() == NetworkingState.Stopping || NetworkingManager.serverNetworking.getNetworkingState() == NetworkingState.Stopped)) {
      return;
    }
    Log.info("Disconnected from " + socketMonitor.getRemoteAddress(), e);
    stop();
  }

  public synchronized void sendToServer(Packet packet) {
    socketMonitor.queue(packet);
  }
}

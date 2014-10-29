package ethanjones.modularworld.networking.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.Networking;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.packet.Packet;
import ethanjones.modularworld.networking.packets.PacketClick;
import ethanjones.modularworld.networking.packets.PacketPlayerInfo;
import ethanjones.modularworld.networking.socket.SocketMonitor;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.common.ModularWorld;

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
    if (getNetworkingState() != NetworkingState.Running) ModularWorld.quit(false);

    if (!ModularWorldClient.instance.player.position.equals(prevPosition) || !ModularWorldClient.instance.player.angle.equals(prevDirection)) {
      PacketPlayerInfo packetPlayerInfo = new PacketPlayerInfo();
      packetPlayerInfo.angle = ModularWorldClient.instance.player.angle;
      packetPlayerInfo.position = ModularWorldClient.instance.player.position;
      sendToServer(packetPlayerInfo);
      prevPosition.set(ModularWorldClient.instance.player.position);
      prevDirection.set(ModularWorldClient.instance.player.angle);
    }

    PacketClick packet = new PacketClick();
    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
      packet.type = PacketClick.Click.get(Input.Buttons.LEFT);
    }
    if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
      packet.type = PacketClick.Click.get(Input.Buttons.MIDDLE);
    }
    if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
      packet.type = PacketClick.Click.get(Input.Buttons.RIGHT);
    }
    if (packet.type != null) {
      NetworkingManager.clientNetworking.sendToServer(packet);
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

  public synchronized void sendToServer(Packet packet) {
    socketMonitor.queue(packet);
  }

  @Override
  public synchronized void disconnected(SocketMonitor socketMonitor, Exception e) {
    if (getNetworkingState() == NetworkingState.Stopping || getNetworkingState() == NetworkingState.Stopped)
      return;
    if (NetworkingManager.serverNetworking != null && (NetworkingManager.serverNetworking.getNetworkingState() == NetworkingState.Stopping || NetworkingManager.serverNetworking.getNetworkingState() == NetworkingState.Stopped))
      return;
    Log.info("Disconnected from " + socketMonitor.getRemoteAddress(), e);
    stop();
  }
}

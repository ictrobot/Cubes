package ethanjones.cubes.networking.client;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.menus.ClientErrorMenu.DisconnectedMenu;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketQueue;
import ethanjones.cubes.networking.packets.PacketConnect;
import ethanjones.cubes.networking.packets.PacketPingRequest;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ClientNetworking extends Networking {
  
  public static final int PING_SECONDS = 10;
  public static final int PING_TICKS = PING_SECONDS * (1000 / Cubes.tickMS);
  public static final long PING_NANOSECONDS = PING_SECONDS * 1000000000L;
  
  //TODO: Send packet when disconnecting and log better
  public static PingResult ping(ClientNetworkingParameter clientNetworkingParameter) {
    Log.debug("Pinging Host:" + clientNetworkingParameter.host + " Port:" + clientNetworkingParameter.port);
    Socket socket;
    try {
      socket = Gdx.net.newClientSocket(Protocol.TCP, clientNetworkingParameter.host, clientNetworkingParameter.port, socketHints);
      return ClientConnectionInitializer.ping(socket);
    } catch (Exception e) {
      PingResult pingResult = new PingResult();
      pingResult.failure = true;
      pingResult.exception = e;
      return pingResult;
    }
  }

  private final ClientNetworkingParameter clientNetworkingParameter;
  private SocketMonitor socketMonitor;
  private Socket socket;
  private Exception exception;
  
  private int tickCount;
  public double pingTime = -1;
  public boolean awaitingPingResponse = false;

  public ClientNetworking(ClientNetworkingParameter clientNetworkingParameter) {
    this.clientNetworkingParameter = clientNetworkingParameter;
  }

  public synchronized void preInit() throws Exception {
    setNetworkingState(NetworkingState.Starting);
    Log.info("Starting Client Networking");
    Log.info("Host:" + clientNetworkingParameter.host + " Port:" + clientNetworkingParameter.port);
    Socket socket;
    try {
      socket = Gdx.net.newClientSocket(Protocol.TCP, clientNetworkingParameter.host, clientNetworkingParameter.port, socketHints);
    } catch (GdxRuntimeException e) {
      if (!(e.getCause() instanceof Exception)) throw e;
      throw (Exception) e.getCause();
    }
    try {
      ClientConnectionInitializer.connect(socket);
    } catch (Exception e) {
      try {
        socket.dispose();
      } catch (Exception ignored) {
      }
      throw e;
    }
    this.socket = socket;
    Log.info("Successfully connected");
  }

  @Override
  public void init() {
    socketMonitor = new SocketMonitor(socket, this, Side.Client);
    setNetworkingState(NetworkingState.Running);
    sendPacketToServer(new PacketConnect()); //Has to be running when sending packet
  }

  @Override
  public synchronized void update() {
    if (getNetworkingState() != NetworkingState.Running) {
      Adapter.gotoMenu(exception != null ? new DisconnectedMenu(exception) : new DisconnectedMenu());
    }
    tickCount++;
    if (tickCount % PING_TICKS == 0) {
      if (awaitingPingResponse) {
        disconnected(socketMonitor, new CubesException("No ping response"));
      } else {
        awaitingPingResponse = true;
        sendPacketToServer(new PacketPingRequest());
      }
    }
  }

  @Override
  public synchronized void stop() {
    if (getNetworkingState() != NetworkingState.Running) return;
    setNetworkingState(NetworkingState.Stopping);
    Log.info("Stopping Client Networking");
    socketMonitor.dispose();
  }

  @Override
  public void sendPacketToServer(Packet packet) {
    if (getNetworkingState() != NetworkingState.Running) {
      Log.warning("Cannot send " + packet.toString() + " as " + getNetworkingState().name());
      return;
    }
    PacketDirection.checkPacketSend(packet.getClass(), Side.Client);
    socketMonitor.getSocketOutput().getPacketQueue().add(packet);
  }

  @Override
  public synchronized void disconnected(SocketMonitor socketMonitor, Exception e) {
    if (getNetworkingState() == NetworkingState.Stopping) return;
    this.exception = e;
    Log.info("Disconnected from " + socketMonitor.getSocket().getRemoteAddress(), e);
    stop();
  }

  public void processPackets() {
    Packet packet = null;
    PacketQueue packetQueue = socketMonitor.getSocketInput().getPacketQueue();
    while ((packet = packetQueue.get()) != null) {
      PacketDirection.checkPacketReceive(packet.getClass(), Side.Client);
      packet.handlePacket();
    }
  }
}

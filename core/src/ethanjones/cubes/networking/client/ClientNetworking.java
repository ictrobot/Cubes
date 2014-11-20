package ethanjones.cubes.networking.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.Protocol;
import com.badlogic.gdx.net.NetJavaSocketImpl;
import com.badlogic.gdx.net.Socket;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.networking.Networking;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketBuffer;
import ethanjones.cubes.networking.packet.PacketIDDatabase;
import ethanjones.cubes.networking.packets.PacketConnect;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;

public class ClientNetworking extends Networking {

  private final ClientNetworkingParameter clientNetworkingParameter;
  private PacketBuffer packetBuffer;
  private SocketMonitor socketMonitor;
  private Socket socket;

  public ClientNetworking(ClientNetworkingParameter clientNetworkingParameter) {
    this.clientNetworkingParameter = clientNetworkingParameter;
    this.packetBuffer = new PacketBuffer();
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
    java.net.Socket javaSocket;
    if (socket instanceof NetJavaSocketImpl) {
      try {
        Field f = NetJavaSocketImpl.class.getDeclaredField("socket");
        f.setAccessible(true);
        javaSocket = (java.net.Socket) f.get(socket);
      } catch (Exception e) {
        throw new IOException("Failed to get java socket", e);
      }
    } else {
      throw new IOException("libGDX socket is not a " + NetJavaSocketImpl.class.getSimpleName());
    }
    javaSocket.setSoTimeout(500);
    int serverMajor;
    int serverMinor;
    int serverPoint;
    int serverBuild;
    String serverHash;
    try {
      DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
      serverMajor = dataInputStream.readInt();
      serverMinor = dataInputStream.readInt();
      serverPoint = dataInputStream.readInt();
      serverBuild = dataInputStream.readInt();
      serverHash = dataInputStream.readUTF();
    } catch (IOException e) {
      if (e instanceof SocketTimeoutException) {
        throw new IOException("Server did not respond in time", e);
      } else {
        throw e;
      }
    }
    if (serverMajor == Branding.VERSION_MAJOR && serverMinor == Branding.VERSION_MINOR && serverPoint == Branding.VERSION_POINT) {
      if (serverBuild == Branding.VERSION_BUILD) {
        if (!serverHash.equals(Branding.VERSION_HASH)) {
          Log.warning("Server reports the same build, but has a different hash");
        } else {
          Log.debug("Server is running exactly the same build");
        }
      } else {
        Log.warning("Server is running build " + serverBuild);
      }
    } else {
      String str = serverMajor + "." + serverMinor + "." + serverPoint;
      throw new IOException("Server is running version " + str + " not " + Branding.VERSION_MAJOR_MINOR_POINT);
    }
    javaSocket.setSoTimeout(0);
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
    if (getNetworkingState() != NetworkingState.Running) Adapter.gotoMainMenu();
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
    socketMonitor.getSocketOutput().getPacketQueue().addPacket(packet);
  }

  @Override
  public synchronized void disconnected(SocketMonitor socketMonitor, Exception e) {
    if (getNetworkingState() == NetworkingState.Stopping) return;
    Log.info("Disconnected from " + socketMonitor.getSocket().getRemoteAddress(), e);
    stop();
  }

  public void received(SocketMonitor socketMonitor, Packet packet) {
    packetBuffer.addPacket(packet);
  }

  public void processPackets() {
    packetBuffer.process();
  }
}

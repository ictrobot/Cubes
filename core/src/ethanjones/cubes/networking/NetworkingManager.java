package ethanjones.cubes.networking;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.SocketHints;

import ethanjones.cubes.networking.client.ClientNetworking;
import ethanjones.cubes.networking.client.ClientNetworkingParameter;
import ethanjones.cubes.networking.packets.PacketConnect;
import ethanjones.cubes.networking.server.ServerNetworking;
import ethanjones.cubes.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.side.Side;

public class NetworkingManager {

  public final static ServerSocketHints serverSocketHints;
  public final static SocketHints socketHints;
  public final static Net.Protocol protocol = Net.Protocol.TCP;

  public static ClientNetworking clientNetworking;
  public static ServerNetworking serverNetworking;

  static {
    serverSocketHints = new ServerSocketHints();
    serverSocketHints.acceptTimeout = 0;
    socketHints = new SocketHints();
    socketHints.keepAlive = true;
    socketHints.connectTimeout = 30000;
  }

  public static void connectClient(ClientNetworkingParameter clientNetworkingParameter) {
    clientNetworking = new ClientNetworking(clientNetworkingParameter.host, clientNetworkingParameter.port);
    clientNetworking.start();
    clientNetworking.sendToServer(new PacketConnect());
  }

  public static void startServer(ServerNetworkingParameter serverNetworkingParameter) {
    serverNetworking = new ServerNetworking(serverNetworkingParameter.port);
    serverNetworking.start();
  }

  public static Networking getNetworking(Side side) {
    switch (side) {
      case Client:
        return clientNetworking;
      case Server:
        return serverNetworking;
    }
    return null;
  }
}

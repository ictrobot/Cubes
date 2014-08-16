package ethanjones.modularworld.networking;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.SocketHints;
import ethanjones.modularworld.networking.client.ClientNetworking;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.networking.common.Networking;
import ethanjones.modularworld.networking.packets.PacketConnect;
import ethanjones.modularworld.networking.server.ServerNetworking;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.Side;

public class NetworkingManager {

  //TODO Make networking more built prove, e.g. survive packet read throwing exception

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

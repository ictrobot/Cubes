package ethanjones.cubes.networking;

import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.ServerSocketHints;
import com.badlogic.gdx.net.SocketHints;

import ethanjones.cubes.networking.client.ClientNetworking;
import ethanjones.cubes.networking.client.ClientNetworkingParameter;
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

  public static void clientPreInit(ClientNetworkingParameter clientNetworkingParameter) throws Exception {
    clientNetworking = new ClientNetworking(clientNetworkingParameter);
    clientNetworking.preInit();
  }

  public static void clientInit() {
    clientNetworking.init();
  }

  public static void serverPreInit(ServerNetworkingParameter serverNetworkingParameter) throws Exception {
    serverNetworking = new ServerNetworking(serverNetworkingParameter);
    serverNetworking.preInit();
  }

  public static void serverInit() {
    serverNetworking.init();
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

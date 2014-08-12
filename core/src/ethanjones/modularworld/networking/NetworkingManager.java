package ethanjones.modularworld.networking;

import ethanjones.modularworld.networking.client.ClientNetworking;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.networking.common.Networking;
import ethanjones.modularworld.networking.packets.PacketConnect;
import ethanjones.modularworld.networking.server.ServerNetworking;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.Side;

public class NetworkingManager {

  public static ClientNetworking clientNetworking;
  public static ServerNetworking serverNetworking;

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

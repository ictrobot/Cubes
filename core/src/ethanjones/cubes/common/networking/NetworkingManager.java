package ethanjones.cubes.common.networking;

import ethanjones.cubes.common.logging.Log;
import ethanjones.cubes.common.networking.client.ClientNetworking;
import ethanjones.cubes.common.networking.client.ClientNetworkingParameter;
import ethanjones.cubes.common.networking.packet.Packet;
import ethanjones.cubes.common.networking.server.ClientIdentifier;
import ethanjones.cubes.common.networking.server.ServerNetworking;
import ethanjones.cubes.common.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.common.networking.singleplayer.SingleplayerNetworking;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.Cubes;

public class NetworkingManager {

  private static Networking clientNetworking;
  private static Networking serverNetworking;

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

  public static void singleplayerPreInit() throws Exception {
    clientNetworking = new SingleplayerNetworking();
    serverNetworking = clientNetworking;
    clientNetworking.preInit();
  }

  public static void sendPacketToServer(Packet packet) {
    if (clientNetworking != null) {
      clientNetworking.sendPacketToServer(packet);
    } else {
      Log.warning("Cannot send " + packet.toString() + " as networking not set up yet");
    }
  }

  public static void sendPacketToClient(Packet packet, ClientIdentifier clientIdentifier) {
    if (serverNetworking != null) {
      serverNetworking.sendPacketToClient(packet, clientIdentifier);
    } else {
      Log.warning("Cannot send " + packet.toString() + " as networking not set up yet");
    }
  }

  public static void sendPacketToAllClients(Packet packet) {
    if (serverNetworking != null && Cubes.getServer() != null) {
      for (ClientIdentifier clientIdentifier : Cubes.getServer().getAllClients()) {
        serverNetworking.sendPacketToClient(packet, clientIdentifier);
      }
    } else {
      Log.warning("Cannot send " + packet.toString() + " as networking not set up yet");
    }
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

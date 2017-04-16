package ethanjones.cubes.networking;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.singleplayer.SingleplayerNetworking;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;

import com.badlogic.gdx.utils.reflect.ClassReflection;

public class NetworkingManager {

  private static Networking clientNetworking;
  private static Networking serverNetworking;

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

  public static void sendPacketToOtherClients(Packet packet, ClientIdentifier otherThan) {
    if (serverNetworking != null && Cubes.getServer() != null) {
      for (ClientIdentifier clientIdentifier : Cubes.getServer().getAllClients()) {
        if (clientIdentifier != otherThan) serverNetworking.sendPacketToClient(packet, clientIdentifier);
      }
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
  
  public static boolean isSingleplayer() {
    return ClassReflection.isInstance(SingleplayerNetworking.class, clientNetworking);
  }
}

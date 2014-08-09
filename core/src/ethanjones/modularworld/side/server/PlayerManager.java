package ethanjones.modularworld.side.server;

import ethanjones.modularworld.core.thread.Threads;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.networking.packets.PacketConnect;
import ethanjones.modularworld.side.server.thread.SendWorldCallable;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.coordinates.Coordinates;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.thread.GenerateWorld;

public class PlayerManager {

  private PacketConnect packetConnect;
  private SocketMonitor socketMonitor;
  private Player player;

  private int renderDistance;

  public PlayerManager(PacketConnect packetConnect) {
    ModularWorldServer.instance.playerManagers.add(this);
    this.packetConnect = packetConnect;
    this.socketMonitor = packetConnect.getSocketMonitor();
    this.player = new Player(packetConnect.username); //TODO: Check if known0

    renderDistance = packetConnect.renderDistance;

    initialLoadAreas();
  }

  private void initialLoadAreas() {
    AreaCoordinates coordinates = new Coordinates(player.position);
    AreaReference check = new AreaReference();
    for (int areaX = coordinates.areaX - renderDistance; areaX <= coordinates.areaX + renderDistance; areaX++) {
      check.areaX = areaX;
      for (int areaY = Math.max(coordinates.areaY - renderDistance, 0); areaY <= coordinates.areaY + renderDistance; areaY++) {
        check.areaY = areaY;
        for (int areaZ = coordinates.areaZ - renderDistance; areaZ <= coordinates.areaZ + renderDistance; areaZ++) {
          check.areaZ = areaZ;
          checkAndRequestArea(check);
        }
      }
    }
  }


  private void checkAndRequestArea(AreaReference areaReference) {
    if (ModularWorldServer.instance.world.getAreaInternal(areaReference, false, false) == null)
      requestArea(areaReference.clone());
  }

  private void requestArea(AreaReference areaReference) {
    Threads.execute(new SendWorldCallable(new GenerateWorld(areaReference, (ethanjones.modularworld.world.WorldServer) ModularWorldServer.instance.world), socketMonitor.getSocketOutput().getPacketQueue()));
  }
}

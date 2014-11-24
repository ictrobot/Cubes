package ethanjones.cubes.networking.server;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packets.PacketConnect;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.server.PlayerManager;

public class ClientIdentifier {

  private SocketMonitor socketMonitor;
  private final PlayerManager playerManager;
  private Player player;

  public ClientIdentifier(SocketMonitor socketMonitor, PacketConnect packetConnect) {
    this.socketMonitor = socketMonitor;
    this.player = new Player(packetConnect.username, this);
    this.playerManager = new PlayerManager(this, packetConnect);
  }

  public SocketMonitor getSocketMonitor() {
    return socketMonitor;
  }

  public PlayerManager getPlayerManager() {
    return playerManager;
  }

  public Player getPlayer() {
    return player;
  }
}

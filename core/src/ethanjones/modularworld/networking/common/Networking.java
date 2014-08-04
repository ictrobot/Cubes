package ethanjones.modularworld.networking.common;

import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.networking.common.packet.PacketHandler;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;

public abstract class Networking implements PacketHandler {

  public final int port;

  public Networking(int port) {
    this.port = port;
  }

  public abstract void start();

  public abstract void stop();

  /**
   * @param e may be null
   */
  public void disconnected(SocketMonitor socketMonitor, Exception e) {
    Log.error("Disconnected from " + socketMonitor.getRemoteAddress());
  }

}

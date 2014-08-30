package ethanjones.modularworld.networking.common.packet;

import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.core.data.other.DataParser;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.side.Side;

public abstract class Packet implements DataParser<DataGroup> {

  private final PacketPriority priority;
  private SocketMonitor socketMonitor;
  private Side side;

  public Packet() {
    this(PacketPriority.MEDIUM);
  }

  public Packet(PacketPriority priority) {
    this.priority = priority;
  }

  public PacketPriority getPriority() {
    return priority;
  }

  /**
   * Called right before writing packet into output stream
   *
   * @return if packet should be send
   */
  public boolean shouldSend() {
    return true;
  }

  public abstract void handlePacket();

  public SocketMonitor getSocketMonitor() {
    return socketMonitor;
  }

  protected void setSocketMonitor(SocketMonitor socketMonitor) {
    this.socketMonitor = socketMonitor;
  }

  public Side getSide() {
    return side;
  }

  protected void setSide(Side side) {
    this.side = side;
  }
}

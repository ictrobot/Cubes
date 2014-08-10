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

  public abstract void handlePacket();

  protected void setSocketMonitor(SocketMonitor socketMonitor) {
    this.socketMonitor = socketMonitor;
  }

  public SocketMonitor getSocketMonitor() {
    return socketMonitor;
  }

  protected void setSide(Side side) {
    this.side = side;
  }

  public Side getSide() {
    return side;
  }
}

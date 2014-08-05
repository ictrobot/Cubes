package ethanjones.modularworld.networking.common.packet;

import ethanjones.modularworld.core.data.ByteBase;
import ethanjones.modularworld.core.data.ByteParser;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.networking.common.socket.SocketMonitor;

public abstract class Packet implements ByteParser<ByteBase> {

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

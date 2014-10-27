package ethanjones.modularworld.networking.packet.environment;

import ethanjones.modularworld.networking.socket.SocketMonitor;
import ethanjones.modularworld.side.Side;

public class ReceivingPacketEnvironment extends PacketEnvironment {

  private final SocketMonitor socketMonitor;
  private final Side side;

  public ReceivingPacketEnvironment(SocketMonitor socketMonitor, Side side) {
    this.socketMonitor = socketMonitor;
    this.side = side;
  }

  public SocketMonitor getSocketMonitor() {
    return socketMonitor;
  }

  public Side getSide() {
    return side;
  }

  @Override
  public PacketStatus getStatus() {
    return PacketStatus.Receiving;
  }
}

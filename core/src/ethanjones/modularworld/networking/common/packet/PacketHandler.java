package ethanjones.modularworld.networking.common.packet;

import ethanjones.modularworld.networking.common.socket.SocketMonitor;

public interface PacketHandler {
  public void received(Packet packet, SocketMonitor socketMonitor);
}

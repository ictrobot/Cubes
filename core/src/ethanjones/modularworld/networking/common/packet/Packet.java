package ethanjones.modularworld.networking.common.packet;

import ethanjones.modularworld.networking.common.socket.SocketMonitor;
import ethanjones.modularworld.side.Side;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class Packet {

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

  public abstract void write(DataOutputStream dataOutputStream) throws Exception;

  public abstract void read(DataInputStream dataInputStream) throws Exception;

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

  public void setSocketMonitor(SocketMonitor socketMonitor) {
    this.socketMonitor = socketMonitor;
  }

  public Side getSide() {
    return side;
  }

  protected void setSide(Side side) {
    this.side = side;
  }
}

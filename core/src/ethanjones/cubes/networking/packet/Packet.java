package ethanjones.cubes.networking.packet;

import ethanjones.cubes.networking.socket.SocketMonitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class Packet {

  private PacketPriority packetPriority;
  private SocketMonitor socketMonitor;

  public Packet() {
    packetPriority = PacketPriority.Medium;
  }

  public abstract void write(DataOutputStream dataOutputStream) throws Exception;

  public abstract void read(DataInputStream dataInputStream) throws Exception;

  public abstract void handlePacket();

  /**
   * Called right before writing packet into output stream
   *
   * @return if packet should be send
   */
  public boolean shouldSend() {
    return true;
  }

  public boolean shouldCompress() {
    return false;
  }

  /**
   * Sided.getSide() will return the new side
   */
  public Packet copy() {
    return null;
  }

  public String toString() {
    return this.getClass().getSimpleName();
  }

  public PacketPriority getPacketPriority() {
    return packetPriority;
  }

  public void setPacketPriority(PacketPriority packetPriority) {
    this.packetPriority = packetPriority;
  }

  public SocketMonitor getSocketMonitor() {
    return socketMonitor;
  }

  public void setSocketMonitor(SocketMonitor socketMonitor) {
    this.socketMonitor = socketMonitor;
  }
}

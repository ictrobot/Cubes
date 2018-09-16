package ethanjones.cubes.networking.packet;

import ethanjones.cubes.networking.socket.SocketMonitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class Packet {

  private SocketMonitor socketMonitor;

  public abstract void write(DataOutputStream dataOutputStream) throws IOException;

  public abstract void read(DataInputStream dataInputStream) throws IOException;

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

  public SocketMonitor getSocketMonitor() {
    return socketMonitor;
  }

  public void setSocketMonitor(SocketMonitor socketMonitor) {
    this.socketMonitor = socketMonitor;
  }
}

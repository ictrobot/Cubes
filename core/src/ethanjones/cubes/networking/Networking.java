package ethanjones.cubes.networking;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketBuffer;
import ethanjones.cubes.networking.packet.PacketIDDatabase;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;

public abstract class Networking {

  public static enum NetworkingState {
    Starting, Running, Stopping, Stopped
  }
  private final Side side;
  private PacketBuffer packetBuffer;
  private volatile NetworkingState networkingState;
  private PacketIDDatabase packetIDDatabase;

  public Networking(Side side) {
    this.side = side;
    this.packetBuffer = new PacketBuffer();
    this.packetIDDatabase = new PacketIDDatabase();
  }

  public NetworkingState getNetworkingState() {
    return networkingState;
  }

  protected void setNetworkingState(NetworkingState networkingState) {
    this.networkingState = networkingState;
  }

  public abstract void start();

  public abstract void tick();

  public abstract void stop();

  /**
   * @param e may be null
   */
  public abstract void disconnected(SocketMonitor socketMonitor, Exception e);

  public final void received(Packet packet) {
    packetBuffer.addPacket(packet);
  }

  /**
   * Call on main thread
   */
  public final void processPackets() {
    packetBuffer.process();
  }

  public final Side getSide() {
    return side;
  }

  public PacketIDDatabase getPacketIDDatabase() {
    return packetIDDatabase;
  }
}

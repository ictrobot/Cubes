package ethanjones.cubes.networking.packet.environment;

public abstract class PacketEnvironment {

  public static enum PacketStatus {
    Sending, Receiving
  }

  public abstract PacketStatus getStatus();

  /**
   * May throw a ClassCastException
   */
  public SendingPacketEnvironment getSending() {
    return (SendingPacketEnvironment) this;
  }

  /**
   * May throw a ClassCastException
   */
  public ReceivingPacketEnvironment getReceiving() {
    return (ReceivingPacketEnvironment) this;
  }
}

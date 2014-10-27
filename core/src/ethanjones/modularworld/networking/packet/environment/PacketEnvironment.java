package ethanjones.modularworld.networking.packet.environment;

public abstract class PacketEnvironment {

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

  public static enum PacketStatus {
    Sending, Receiving
  }
}

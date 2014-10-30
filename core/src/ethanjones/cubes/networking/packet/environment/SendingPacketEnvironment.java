package ethanjones.cubes.networking.packet.environment;

public class SendingPacketEnvironment extends PacketEnvironment {

  private PacketPriority packetPriority;
  private PacketSendingType sendingType;

  public SendingPacketEnvironment() {
    this(PacketPriority.Medium, null);
  }

  public SendingPacketEnvironment(PacketPriority packetPriority, PacketSendingType sendingType) {
    this.packetPriority = packetPriority;
    this.sendingType = sendingType;
  }

  public PacketSendingType getSendingType() {
    return sendingType;
  }

  public void setSendingType(PacketSendingType sendingType) {
    this.sendingType = sendingType;
  }

  public PacketPriority getPacketPriority() {
    return packetPriority;
  }

  public void setPacketPriority(PacketPriority packetPriority) {
    this.packetPriority = packetPriority;
  }

  @Override
  public PacketStatus getStatus() {
    return PacketStatus.Sending;
  }
}

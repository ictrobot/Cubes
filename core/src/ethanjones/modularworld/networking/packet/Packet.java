package ethanjones.modularworld.networking.packet;

import ethanjones.modularworld.networking.packet.environment.PacketEnvironment;
import ethanjones.modularworld.networking.packet.environment.SendingPacketEnvironment;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public abstract class Packet {

  private PacketEnvironment packetEnvironment;

  public Packet() {//Always initialised as sending
    setPacketEnvironment(new SendingPacketEnvironment());
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

  public final PacketEnvironment getPacketEnvironment() {
    return packetEnvironment;
  }

  public void setPacketEnvironment(PacketEnvironment packetEnvironment) {
    this.packetEnvironment = packetEnvironment;
  }
}

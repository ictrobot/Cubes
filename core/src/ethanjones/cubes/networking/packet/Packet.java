package ethanjones.cubes.networking.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.networking.packet.environment.PacketEnvironment;
import ethanjones.cubes.networking.packet.environment.SendingPacketEnvironment;

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

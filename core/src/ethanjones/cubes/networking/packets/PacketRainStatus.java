package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.client.WorldClient;
import ethanjones.cubes.world.generator.RainStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

@Direction(PacketDirection.TO_CLIENT)
public class PacketRainStatus extends Packet {

  public RainStatus rainStatus = RainStatus.NOT_RAINING;

  @Override
  public void write(DataOutputStream dataOutputStream) throws IOException {
    dataOutputStream.writeBoolean(rainStatus.raining);
    dataOutputStream.writeFloat(rainStatus.rainRate);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws IOException {
    boolean raining = dataInputStream.readBoolean();
    float rainRate = dataInputStream.readFloat();
    rainStatus = new RainStatus(raining, rainRate);
  }

  @Override
  public void handlePacket() {
    ((WorldClient) Cubes.getClient().world).rainStatus = rainStatus;
  }

  @Override
  public Packet copy() {
    PacketRainStatus p = new PacketRainStatus();
    p.rainStatus = rainStatus;
    return p;
  }

  @Override
  public String toString() {
    return super.toString() + " " + rainStatus.toString();
  }
}

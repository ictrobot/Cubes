package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class PacketMovementSpeed extends Packet {

  public float speed;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeFloat(speed);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    speed = dataInputStream.readFloat();
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Client) {
      Cubes.getClient().inputChain.cameraController.setSpeed(speed);
    }
  }

  @Override
  public String toString() {
    return super.toString() + " " + speed;
  }
}

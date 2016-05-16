package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_CLIENT)
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
    Cubes.getClient().inputChain.cameraController.setSpeed(speed);
  }

  @Override
  public String toString() {
    return super.toString() + " " + speed;
  }
}

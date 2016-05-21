package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.storage.Area;

import java.io.DataInputStream;
import java.io.DataOutputStream;

@Direction(PacketDirection.TO_CLIENT)
@Priority(PacketPriority.HIGH)
public class PacketAreaUpdateRender extends Packet {
  public int areaX;
  public int areaZ;
  public int ySection;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    dataOutputStream.writeInt(areaX);
    dataOutputStream.writeInt(areaZ);
    dataOutputStream.writeInt(ySection);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    areaX = dataInputStream.readInt();
    areaZ = dataInputStream.readInt();
    ySection = dataInputStream.readInt();
  }

  @Override
  public void handlePacket() {
    Area area = Cubes.getClient().world.getArea(areaX, areaZ);
    if (area != null) area.updateRender(ySection);
  }
}

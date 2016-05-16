package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

@Direction(PacketDirection.TO_CLIENT)
public class PacketEntityUpdate extends DataPacket {
  public DataGroup data;

  @Override
  public void handlePacket() {
    Cubes.getClient().world.updateEntity(data);
  }

  @Override
  public DataGroup write() {
    return data;
  }

  @Override
  public void read(DataGroup data) {
    this.data = data;
  }

  @Override
  public String toString() {
    return super.toString() + " " + data.get("uuid").toString();
  }
}

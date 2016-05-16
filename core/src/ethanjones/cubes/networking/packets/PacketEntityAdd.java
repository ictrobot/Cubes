package ethanjones.cubes.networking.packets;

import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

@Direction(PacketDirection.TO_CLIENT)
public class PacketEntityAdd extends DataPacket {
  public Entity entity;

  @Override
  public void handlePacket() {
    Cubes.getClient().world.addEntity(entity);
  }

  @Override
  public DataGroup write() {
    return entity.write();
  }

  @Override
  public void read(DataGroup data) {
    entity = Entity.readType(data);
  }

  @Override
  public String toString() {
    return super.toString() + " " + entity.toString();
  }
}

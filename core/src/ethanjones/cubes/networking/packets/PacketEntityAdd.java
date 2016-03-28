package ethanjones.cubes.networking.packets;

import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

public class PacketEntityAdd extends DataPacket {
  public Entity entity;

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Client) {
      Cubes.getClient().world.addEntity(entity);
    } else if (Sided.getSide() == Side.Server) {
      Cubes.getServer().world.addEntity(entity);
    }
  }

  @Override
  public DataGroup write() {
    return entity.write();
  }

  @Override
  public void read(DataGroup data) {
    entity = Entity.readType(data);
  }
}

package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.gwt.UUID;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

@Direction(PacketDirection.TO_CLIENT)
public class PacketEntityRemove extends DataPacket {
  public UUID uuid;

  @Override
  public void handlePacket() {
    Cubes.getClient().world.removeEntity(uuid);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("uuid", uuid);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    uuid = (UUID) data.get("uuid");
  }

  @Override
  public String toString() {
    return super.toString() + " " + uuid.toString();
  }
}

package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

import java.util.UUID;

public class PacketEntityRemove extends DataPacket {
  public UUID uuid;

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Client) {
      Cubes.getClient().world.removeEntity(uuid);
    } else if (Sided.getSide() == Side.Server) {
      Cubes.getServer().world.removeEntity(uuid);
    }
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
}

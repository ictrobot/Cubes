package ethanjones.cubes.networking.packets;

import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

import java.util.UUID;

public class PacketEntityUpdate extends DataPacket {
  public DataGroup data;

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Client) {
      Cubes.getClient().world.updateEntity(data);
    } else if (Sided.getSide() == Side.Server) {
      Cubes.getServer().world.updateEntity(data);
    }
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

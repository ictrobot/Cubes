package ethanjones.cubes.networking.packets;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

import java.util.UUID;

public class PacketConnected extends DataPacket {

  public DataGroup idManager;
  public UUID player;

  public PacketConnected() {
    setPacketPriority(PacketPriority.High);
  }

  @Override
  public void handlePacket() {
    if (Sided.getSide() == Side.Client) {
      Sided.getIDManager().read(idManager);
      Player player = Cubes.getClient().player;
      player.uuid = this.player;
      player.addToWorld();
    }
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("idManager", idManager);
    dataGroup.put("player", player);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    idManager = dataGroup.getGroup("idManager");
    player = (UUID) dataGroup.get("player");
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

package ethanjones.cubes.networking.packets;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

import java.util.UUID;

@Direction(PacketDirection.TO_CLIENT)
public class PacketConnected extends DataPacket {

  public DataGroup idManager;
  public UUID player;
  public int worldTime;

  public PacketConnected() {
    setPacketPriority(PacketPriority.High);
  }

  @Override
  public void handlePacket() {
    Sided.getIDManager().read(idManager);
    Player player = Cubes.getClient().player;
    player.uuid = this.player;
    player.addToWorld();
    Cubes.getClient().world.time = worldTime;
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("idManager", idManager);
    dataGroup.put("player", player);
    dataGroup.put("worldTime", worldTime);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    idManager = dataGroup.getGroup("idManager");
    player = (UUID) dataGroup.get("player");
    worldTime = dataGroup.getInteger("worldTime");
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

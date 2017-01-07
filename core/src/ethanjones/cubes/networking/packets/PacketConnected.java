package ethanjones.cubes.networking.packets;

import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.networking.packet.PacketPriority;
import ethanjones.cubes.networking.packet.PacketPriority.Priority;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.save.Gamemode;
import ethanjones.data.DataGroup;

import java.util.UUID;

@Direction(PacketDirection.TO_CLIENT)
@Priority(PacketPriority.HIGH)
public class PacketConnected extends DataPacket {

  public DataGroup idManager;
  public UUID player;
  public int worldTime;
  public Gamemode gamemode;

  @Override
  public void handlePacket() {
    if (!NetworkingManager.isSingleplayer()) {
      IDManager.resetMapping();
      IDManager.readMapping(idManager);
    }
    Player player = Cubes.getClient().player;
    player.uuid = this.player;
    player.addToWorld();
    Cubes.getClient().world.setTime(worldTime);
    Cubes.getClient().gamemode = gamemode;
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("idManager", idManager);
    dataGroup.put("player", player);
    dataGroup.put("worldTime", worldTime);
    dataGroup.put("worldGamemode", gamemode.name());
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    idManager = dataGroup.getGroup("idManager");
    player = (UUID) dataGroup.get("player");
    worldTime = dataGroup.getInteger("worldTime");
    gamemode = Gamemode.valueOf(dataGroup.getString("worldGamemode"));
  }

  @Override
  public String toString() {
    return super.toString();
  }
}

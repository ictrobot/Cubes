package ethanjones.cubes.networking.packets;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.networking.packet.PacketDirection;
import ethanjones.cubes.networking.packet.PacketDirection.Direction;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.data.DataGroup;

@Direction(PacketDirection.OMNIDIRECTIONAL)
public class PacketPlayerInventory extends DataPacket {
  public DataGroup inv;

  @Override
  public void handlePacket() {
    Player player;
    if (Side.isServer()) {
      player = Cubes.getServer().getClient(getSocketMonitor()).getPlayer();
    } else {
      player = Cubes.getClient().player;
    }
    player.getInventory().read(inv);
  }

  @Override
  public DataGroup write() {
    return inv;
  }

  @Override
  public void read(DataGroup data) {
    this.inv = data;
  }

  @Override
  public boolean shouldCompress() {
    return true;
  }
}

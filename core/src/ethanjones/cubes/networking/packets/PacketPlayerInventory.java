package ethanjones.cubes.networking.packets;

import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.DataPacket;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.data.DataGroup;

public class PacketPlayerInventory extends DataPacket {
  public DataGroup inv;

  @Override
  public void handlePacket() {
    Player player;
    if (Sided.getSide() == Side.Server) {
      player = Cubes.getServer().getClient(getSocketMonitor()).getPlayer();
    } else if (Sided.getSide() == Side.Client) {
      player = Cubes.getClient().player;
    } else {
      return;
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

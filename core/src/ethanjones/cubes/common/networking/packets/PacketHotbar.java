package ethanjones.cubes.common.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import ethanjones.cubes.common.block.Block;
import ethanjones.cubes.common.entity.living.player.Player;
import ethanjones.cubes.common.networking.packet.Packet;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.common.Sided;
import ethanjones.cubes.Cubes;

public class PacketHotbar extends Packet {

  public Block[] blocks = new Block[10];
  public int selected;

  @Override
  public void write(DataOutputStream dataOutputStream) throws Exception {
    for (int i = 0; i < 10; i++) {
      dataOutputStream.writeInt(Sided.getBlockManager().toInt(blocks[i]));
    }
    dataOutputStream.writeInt(selected);
  }

  @Override
  public void read(DataInputStream dataInputStream) throws Exception {
    for (int i = 0; i < 10; i++) {
      blocks[i] = Sided.getBlockManager().toBlock(dataInputStream.readInt());
    }
    selected = dataInputStream.readInt();
  }

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
    player.setHotbarNoUpdate(blocks, selected);
  }
}

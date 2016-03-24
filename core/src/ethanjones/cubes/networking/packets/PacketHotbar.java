package ethanjones.cubes.networking.packets;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.packet.Packet;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Arrays;

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

  @Override
  public String toString() {
    return super.toString() + " " + selected + " " + Arrays.toString(blocks);
  }
}

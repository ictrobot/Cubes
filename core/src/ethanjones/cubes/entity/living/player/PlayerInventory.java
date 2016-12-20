package ethanjones.cubes.entity.living.player;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.Items;
import ethanjones.cubes.item.inv.Inventory;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketPlayerInventory;
import ethanjones.cubes.side.common.Side;
import ethanjones.data.DataGroup;

public class PlayerInventory extends Inventory {
  public int hotbarSelected;
  public Player player;

  public PlayerInventory(Player player) {
    super("core:player", 36);
    this.hotbarSelected = 0;
    this.player = player;
    // start with tools
    this.itemStacks[0] = new ItemStack(Items.pickaxe);
    this.itemStacks[1] = new ItemStack(Items.axe);
    this.itemStacks[2] = new ItemStack(Items.shovel);
  }

  public ItemStack selectedItemStack() {
    return itemStacks[hotbarSelected];
  }

  @Override
  public void sync() {
    PacketPlayerInventory packet = new PacketPlayerInventory();
    packet.inv = write();
    if (Side.isClient()) {
      NetworkingManager.sendPacketToServer(packet);
    } else {
      NetworkingManager.sendPacketToClient(packet, player.clientIdentifier);
    }
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = super.write();
    dataGroup.put("selected", hotbarSelected);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    super.read(dataGroup);
    hotbarSelected = dataGroup.getInteger("selected");
  }
}

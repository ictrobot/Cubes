package ethanjones.cubes.entity.living.player;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.Inventory;
import ethanjones.data.DataGroup;

public class PlayerInventory extends Inventory {
  public int hotbarSelected;

  public PlayerInventory() {
    super(40);
    hotbarSelected = 0;
  }

  public ItemStack selectedItemStack() {
    return itemStacks[hotbarSelected];
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

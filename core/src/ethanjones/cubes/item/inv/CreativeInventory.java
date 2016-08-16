package ethanjones.cubes.item.inv;

import ethanjones.cubes.core.IDManager;
import ethanjones.cubes.item.Item;
import ethanjones.cubes.item.ItemBlock;
import ethanjones.cubes.item.ItemStack;
import ethanjones.data.DataGroup;

import java.util.ArrayList;

public class CreativeInventory extends Inventory {

  public CreativeInventory() {
    super("core:creative");

    fixed = true;
    voidItems = true;
    ArrayList<ItemStack> list = new ArrayList<ItemStack>();
    for (ItemBlock itemBlock : IDManager.getItemBlocks()) {
      for (int j : itemBlock.block.displayMetaValues()) {
        list.add(new ItemStack(itemBlock, itemBlock.getStackCountMax(), j));
      }
    }
    for (Item item : IDManager.getItems()) {
      list.add(new ItemStack(item, item.getStackCountMax()));
    }
    while (list.size() % 9 != 0) {
      list.add(null);
    }
    itemStacks = list.toArray(new ItemStack[list.size()]);
  }

  @Override
  public void read(DataGroup dataGroup) {

  }

  @Override
  public DataGroup write() {
    return new DataGroup();
  }
}

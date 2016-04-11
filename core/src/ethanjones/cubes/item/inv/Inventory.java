package ethanjones.cubes.item.inv;

import ethanjones.cubes.item.ItemStack;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

public class Inventory implements DataParser {
  public ItemStack[] itemStacks;

  public Inventory() {

  }

  public Inventory(int size) {
    itemStacks = new ItemStack[size];
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    DataGroup[] groups = new DataGroup[itemStacks.length];
    for (int i = 0; i < itemStacks.length; i++) {
      if (itemStacks[i] == null) {
        groups[i] = new DataGroup();
      } else {
        groups[i] = itemStacks[i].write();
      }
    }
    dataGroup.put("itemstacks", groups);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    DataGroup[] groups = dataGroup.getArray("itemstacks", DataGroup.class);
    if (itemStacks.length != groups.length) itemStacks = new ItemStack[groups.length];
    for (int i = 0; i < itemStacks.length; i++) {
      DataGroup d = groups[i];
      if (d.size() > 0) {
        itemStacks[i] = new ItemStack();
        itemStacks[i].read(d);
      } else {
        itemStacks[i] = null;
      }
    }
  }

  public void sync() {

  }
}

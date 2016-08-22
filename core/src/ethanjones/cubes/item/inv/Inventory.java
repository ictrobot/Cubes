package ethanjones.cubes.item.inv;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.item.ItemStack;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

public class Inventory implements DataParser {
  protected final String name;
  public ItemStack[] itemStacks;
  public int width = 9;
  public boolean fixed = false;
  public boolean voidInputItems = false;
  public boolean cancelInputItems = false;

  public Inventory(String name) {
    this.name = name;
  }

  public Inventory(String name, int size) {
    this.name = name;
    itemStacks = new ItemStack[size];
  }

  public String getDisplayName() {
    return Localization.get("inventory." + name.replaceFirst(":", "."));
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

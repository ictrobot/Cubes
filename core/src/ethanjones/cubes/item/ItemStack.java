package ethanjones.cubes.item;

import ethanjones.cubes.side.Sided;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

public class ItemStack implements DataParser {
  public Item item;
  public int count;

  public ItemStack() {
  }

  public ItemStack(Item item, int count) {
    this.item = item;
    this.count = count;
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("item", Sided.getIDManager().toInt(item));
    dataGroup.put("count", count);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    item = Sided.getIDManager().toItem(dataGroup.getInteger("item"));
    count = dataGroup.getInteger("count");
  }
}

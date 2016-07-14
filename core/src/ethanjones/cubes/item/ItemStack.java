package ethanjones.cubes.item;

import ethanjones.cubes.side.Sided;
import ethanjones.data.DataGroup;
import ethanjones.data.DataParser;

public class ItemStack implements DataParser {
  public Item item;
  public int count;
  public int meta;

  public ItemStack() {
  }

  public ItemStack(Item item) {
    this.item = item;
    this.count = 1;
    this.meta = 0;
  }

  public ItemStack(Item item, int count) {
    this.item = item;
    this.count = count;
    this.meta = 0;
  }

  public ItemStack(Item item, int count, int meta) {
    this.item = item;
    this.count = count;
    this.meta = meta;
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("item", Sided.getIDManager().toInt(item));
    dataGroup.put("count", count);
    if (meta != 0) dataGroup.put("meta", meta);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    item = Sided.getIDManager().toItem(dataGroup.getInteger("item"));
    count = dataGroup.getInteger("count");
    meta = dataGroup.containsKey("meta") ? dataGroup.getInteger("meta") : 0;
  }
}

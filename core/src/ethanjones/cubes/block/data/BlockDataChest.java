package ethanjones.cubes.block.data;

import ethanjones.cubes.item.inv.Inventory;
import ethanjones.cubes.world.storage.Area;
import ethanjones.data.DataGroup;

public class BlockDataChest extends BlockData {
  public Inventory inventory;

  public BlockDataChest(Area area, int x, int y, int z) {
    super(area, x, y, z);
    inventory = new Inventory("core:chest", 27) {

      @Override
      public void sync() {
        BlockDataChest.this.sync();
      }
    };
  }

  @Override
  public DataGroup write() {
    return inventory.write();
  }

  @Override
  public void read(DataGroup dataGroup) {
    inventory.read(dataGroup);
  }
}

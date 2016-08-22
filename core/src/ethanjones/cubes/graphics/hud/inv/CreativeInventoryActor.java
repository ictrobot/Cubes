package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.item.inv.CreativeInventory;

public class CreativeInventoryActor extends ScrollInventoryActor {

  public CreativeInventoryActor() {
    super(new CreativeInventory(), 3);

    inner.add().height(8f).colspan(9);
    inner.row();
    inner.add(new CraftingInventoryActor(false)).space(0).colspan(9);
  }

}

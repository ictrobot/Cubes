package ethanjones.cubes.graphics.hud.inv;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.item.inv.Inventory;

public class InventoryActor extends Table {

  public InventoryActor(Inventory inventory) {
    defaults().space(4f);

    add(new Label(inventory.getDisplayName(), new LabelStyle(Fonts.hud, Color.WHITE))).colspan(inventory.width);
    row();

    for (int i = 0; i < inventory.itemStacks.length; i++) {
      SlotActor slotActor = new SlotActor(inventory, i);
      add(slotActor);

      if ((i + 1) % inventory.width == 0) {
        row();
      }
    }

    pack();
  }
}

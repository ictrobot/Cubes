package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.item.inv.Inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ScrollInventoryActor extends Table {

  private static final float CALIBRATION_PER_ROW = 40f;

  public ScrollInventoryActor(Inventory inventory, int slots) {
    defaults().space(4f);

    add(new Label(inventory.getDisplayName(), new LabelStyle(Fonts.hud, Color.WHITE)));
    row();

    Table table = new Table();
    table.defaults().space(4f);
    for (int i = 0; i < inventory.itemStacks.length; i++) {
      SlotActor slotActor = new SlotActor(inventory, i);
      table.add(slotActor);

      if ((i + 1) % inventory.width == 0) {
        table.row();
      }
    }
    table.pack();

    ScrollPane scrollPane = new ScrollPane(table);
    scrollPane.setScrollingDisabled(true, false);
    add(scrollPane).height(slots * CALIBRATION_PER_ROW).fill();
    row();
    pack();
  }
}

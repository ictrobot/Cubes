package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.item.inv.CraftingInventory;
import ethanjones.cubes.item.inv.InventoryHelper;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class CraftingInventoryActor extends Table {

  CraftingInventory c;

  public CraftingInventoryActor() {
    this(true);
  }

  public CraftingInventoryActor(boolean name) {
    c = new CraftingInventory();
    defaults().space(4f);

    if (name) {
      add(new Label(c.getDisplayName(), new LabelStyle(Fonts.hud, Color.WHITE))).colspan(9);
      row();
    }

    add().uniform();
    add().uniform();
    add(s(0)).uniform();
    add(s(1)).uniform();
    add(s(2)).uniform();
    add().uniform();
    add().uniform();
    add().uniform();
    add().uniform();
    row();

    add().uniform();
    add().uniform();
    add(s(3)).uniform();
    add(s(4)).uniform();
    add(s(5)).uniform();
    add().uniform();
    add(new SlotActor(c.output, 0)).uniform();
    add().uniform();
    add().uniform();
    row();

    add().uniform();
    add().uniform();
    add(s(6)).uniform();
    add(s(7)).uniform();
    add(s(8)).uniform();
    add().uniform();
    add().uniform();
    add().uniform();
    add().uniform();
    row();

    pack();
  }

  @Override
  protected void setStage(Stage stage) {
    if (stage == null) {
      for (int i = 0; i < 9; i++) {
        if (c.itemStacks[i] != null)
          InventoryHelper.addItemstack(Cubes.getClient().player.getInventory(), c.itemStacks[i]);
      }
    }
    super.setStage(stage);
  }

  private SlotActor s(int i) {
    return new SlotActor(c, i);
  }

}

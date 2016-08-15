package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.item.inv.Inventory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class InventoryActor extends Window {
  private static final NinePatchDrawable background = new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("core:hud/inv/Background.png"), 4, 4, 4, 4));
  public static final WindowStyle style = new WindowStyle(Fonts.hud, Color.BLACK, background);

  public InventoryActor(Inventory inventory) {
    super(inventory.getDisplayName(), style);
    setMovable(false);

    defaults().space(4f);
    pad(38f, 4f, 4f, 4f);

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

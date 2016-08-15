package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class InventoryWindow extends Window {
  private static final NinePatchDrawable background = new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("core:hud/inv/Background.png"), 4, 4, 4, 4));
  public static final WindowStyle style = new WindowStyle(Fonts.hud, Color.BLACK, background);

  public InventoryWindow(InventoryActor actor) {
    super("", style);
    pad(4f, 4f, 4f, 4f);
    //getTitleTable().removeActor(getTitleLabel());
    setMovable(false);
    add(actor).fill().expand();
    pack();
  }
}

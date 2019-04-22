package ethanjones.cubes.graphics.hud.inv;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;

public class InventoryWindow extends Window {
  private static final NinePatchDrawable windowBackground = new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("core:hud/inv/Background.png"), 4, 4, 4, 4));
  private static final WindowStyle style = new WindowStyle(Fonts.hud, Color.BLACK, windowBackground);

  public InventoryWindow(Actor actor) {
    super("", style);
    pad(4f, 4f, 4f, 4f);
    //getTitleTable().removeActor(getTitleLabel());
    setMovable(false);
    add(actor).fill().expand();
    pack();
  }
}

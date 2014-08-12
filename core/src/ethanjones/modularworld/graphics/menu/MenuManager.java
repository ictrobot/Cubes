package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.side.common.ModularWorld;

public class MenuManager {

  protected Skin skin;

  public void start() {
    ModularWorld.setup();
    Gdx.graphics.setTitle(Branding.DEBUG);

    skin = new Skin();
    skin.add("default", new BitmapFont());
    skin.add("default", new Label.LabelStyle(skin.getFont("default"), Color.WHITE));

    NinePatch buttonDown = new NinePatch(GraphicsHelper.getTexture("hud/ButtonDown.png").textureRegion, 8, 8, 8, 8);
    NinePatch buttonUp = new NinePatch(GraphicsHelper.getTexture("hud/ButtonUp.png").textureRegion, 8, 8, 8, 8);
    skin.add("default", new TextButton.TextButtonStyle(new NinePatchDrawable(buttonUp), new NinePatchDrawable(buttonDown), null, skin.getFont("default")));
  }
}

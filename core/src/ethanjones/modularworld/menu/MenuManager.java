package ethanjones.modularworld.menu;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.wrapper.AdaptiveApplicationListener;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.side.common.ModularWorld;

public class MenuManager extends Game {

  public final Compatibility compatibility;
  public final AdaptiveApplicationListener adaptiveApplicationListener;

  public MenuManager(Compatibility compatibility, AdaptiveApplicationListener adaptiveApplicationListener) {
    this.compatibility = compatibility;
    this.adaptiveApplicationListener = adaptiveApplicationListener;
  }

  protected Skin skin;

  @Override
  public void create() {
    ModularWorld.setup();

    skin = new Skin();
    skin.add("default", new BitmapFont());
    skin.add("default", new Label.LabelStyle(skin.getFont("default"), Color.WHITE));


    NinePatch buttonDown = new NinePatch(GraphicsHelper.getTexture("hud/ButtonDown.png").textureRegion, 8, 8, 8, 8);
    NinePatch buttonUp = new NinePatch(GraphicsHelper.getTexture("hud/ButtonUp.png").textureRegion, 8, 8, 8, 8);
    skin.add("default", new TextButton.TextButtonStyle(new NinePatchDrawable(buttonUp), new NinePatchDrawable(buttonDown), null, skin.getFont("default")));

    setScreen(new MainMenu(this));
  }

  public void setScreen(Screen screen) {
    Screen old = this.getScreen();
    super.setScreen(screen);
    if (old != null) old.dispose();
  }
}

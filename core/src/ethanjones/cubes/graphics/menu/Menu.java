package ethanjones.cubes.graphics.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.input.InputChain;

public abstract class Menu {

  public static final Skin skin;
  protected static Stage stage;
  private static SpriteBatch spriteBatch;
  private static ScreenViewport viewport;

  static {
    skin = new Skin();
    skin.add("default", Fonts.Size3, BitmapFont.class);
    skin.add("default", new Label.LabelStyle(skin.getFont("default"), Color.WHITE));
    skin.add("title", new Label.LabelStyle(Fonts.Size7, Color.WHITE));

    NinePatch buttonDown = new NinePatch(Assets.getTextureRegion("core:hud/ButtonDown.png"), 8, 8, 8, 8);
    NinePatch buttonUp = new NinePatch(Assets.getTextureRegion("core:hud/ButtonUp.png"), 8, 8, 8, 8);
    skin.add("default", new TextButton.TextButtonStyle(new NinePatchDrawable(buttonUp), new NinePatchDrawable(buttonDown), null, skin.getFont("default")));
    skin.add("tiny", new TextButton.TextButtonStyle(new NinePatchDrawable(buttonUp), new NinePatchDrawable(buttonDown), null, Fonts.Size1));

    skin.add("default", new TextField.TextFieldStyle(skin.getFont("default"), Color.BLACK, new TextureRegionDrawable(Assets.getTextureRegion("core:hud/TextFieldCursor.png")), new TextureRegionDrawable(Assets.getTextureRegion("core:hud/TextFieldSelection.png")), new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("core:hud/TextFieldBackground.png"), 8, 8, 8, 8))));

    skin.add("default-horizontal", new Slider.SliderStyle(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("core:hud/SliderBackground.png"), 8, 8, 8, 8)), new TextureRegionDrawable(Assets.getTextureRegion("core:hud/SliderKnob.png"))));

    skin.add("default", new ScrollPane.ScrollPaneStyle());

    skin.add("default", new TouchpadStyle(new TextureRegionDrawable(Assets.getTextureRegion("core:hud/TouchpadBackground.png")), new TextureRegionDrawable(Assets.getTextureRegion("core:hud/TouchpadKnob.png"))));

    spriteBatch = new SpriteBatch();
    viewport = new ScreenViewport();

    stage = new Stage(viewport, spriteBatch);
  }

  public static void staticDispose() {
    spriteBatch.dispose();
    stage.dispose();
  }

  public Menu() {

  }

  public void resize(int width, int height) {
    viewport.update(width, height, true);
  }

  public void render() {
    stage.act();
    stage.draw();
  }

  public void hide() {
    InputChain.getInputMultiplexer().removeProcessor(stage);
    stage.clear();
  }

  public void show() {
    InputChain.getInputMultiplexer().addProcessor(0, stage);
    addActors();
  }

  /**
   * Have to add actors to the stage here
   */
  public abstract void addActors();
}

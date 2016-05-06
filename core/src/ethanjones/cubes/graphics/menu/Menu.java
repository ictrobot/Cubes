package ethanjones.cubes.graphics.menu;

import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static ethanjones.cubes.graphics.Graphics.screenViewport;
import static ethanjones.cubes.graphics.Graphics.spriteBatch;

public class Menu {

  public static final Skin skin;

  static {
    skin = new Skin();
    skin.add("default", Fonts.menu, BitmapFont.class);
    skin.add("default", new Label.LabelStyle(skin.getFont("default"), Color.WHITE));
    skin.add("title", new Label.LabelStyle(Fonts.title, Color.WHITE));

    NinePatch buttonDown = new NinePatch(Assets.getTextureRegion("core:hud/ButtonDown.png"), 8, 8, 8, 8);
    NinePatch buttonUp = new NinePatch(Assets.getTextureRegion("core:hud/ButtonUp.png"), 8, 8, 8, 8);
    skin.add("default", new TextButton.TextButtonStyle(new NinePatchDrawable(buttonUp), new NinePatchDrawable(buttonDown), null, skin.getFont("default")));
    skin.add("tiny", new TextButton.TextButtonStyle(new NinePatchDrawable(buttonUp), new NinePatchDrawable(buttonDown), null, Fonts.hud));

    skin.add("default", new TextField.TextFieldStyle(skin.getFont("default"), Color.BLACK, new TextureRegionDrawable(Assets.getTextureRegion("core:hud/TextFieldCursor.png")), new TextureRegionDrawable(Assets.getTextureRegion("core:hud/TextFieldSelection.png")), new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("core:hud/TextFieldBackground.png"), 8, 8, 8, 8))));

    skin.add("default-horizontal", new Slider.SliderStyle(new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("core:hud/SliderBackground.png"), 8, 8, 8, 8)), new TextureRegionDrawable(Assets.getTextureRegion("core:hud/SliderKnob.png"))));

    skin.add("default", new ScrollPane.ScrollPaneStyle());

    skin.add("default", new TouchpadStyle(new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/TouchpadBackground.png")), new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/TouchpadKnob.png"))));
  }

  public Stage stage;

  public Menu() {
    stage = new Stage(screenViewport, spriteBatch);
  }

  public void resize(int width, int height) {

  }

  public void render() {
    stage.act();
    stage.draw();
  }

  public void save() {

  }

  public void dispose() {
    stage.dispose();
  }
}

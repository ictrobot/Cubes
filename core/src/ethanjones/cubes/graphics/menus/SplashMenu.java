package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class SplashMenu extends Menu {
  Image logo;
  Label text;
  
  public SplashMenu() {
    logo = new Image(new TextureRegionDrawable(Assets.getTextureRegion("core:logo.png")), Scaling.fillY, Align.center);
    text = new Label("Loading " + Branding.DEBUG, new Label.LabelStyle(Fonts.smallHUD, Color.WHITE));
  
    stage.addActor(logo);
    stage.addActor(text);
  }
  
  
  @Override
  public void resize(float width, float height) {
    super.resize(width, height);
    logo.setSize(width, height / 6);
    MenuTools.center(logo);
    text.setSize(text.getPrefWidth(), text.getPrefHeight());
    text.setPosition((width / 2) - (text.getWidth() / 2), 2);
    text.setAlignment(Align.center);
  }
}

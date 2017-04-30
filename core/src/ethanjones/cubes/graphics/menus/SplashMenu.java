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
  Label version;
  Label author;
  
  public SplashMenu() {
    logo = new Image(new TextureRegionDrawable(Assets.getTextureRegion("core:logo.png")), Scaling.fillY, Align.center);
    version = new Label(Branding.DEBUG, new Label.LabelStyle(Fonts.smallHUD, Color.WHITE));
    author = new Label(Branding.AUTHOR, new Label.LabelStyle(Fonts.smallHUD, Color.WHITE));
  
    stage.addActor(logo);
    stage.addActor(version);
    stage.addActor(author);
  }
  
  
  @Override
  public void resize(float width, float height) {
    super.resize(width, height);
    logo.setSize(width, height / 6);
    MenuTools.center(logo);
    version.setBounds(2, 2, author.getPrefWidth(), author.getPrefHeight());
    version.setAlignment(Align.left);
    author.setBounds(width - author.getPrefWidth() - 2, 2, author.getPrefWidth(), author.getPrefHeight());
    author.setAlignment(Align.right);
  }
}

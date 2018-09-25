package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.logging.loggers.FileLogWriter;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class LogMenu extends Menu {
  ScrollPane scrollPane;
  Label label;
  TextButton back;
  
  public LogMenu() {
    label = new Label("", new LabelStyle(Fonts.smallHUD, Color.BLACK));
    label.setWrap(true);
    Drawable background = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/LogBackground.png"));
    scrollPane = new ScrollPane(label, new ScrollPaneStyle(background, null, null, null, null));
    scrollPane.setScrollingDisabled(true, false);
    back = MenuTools.getBackButton(this);
    
    stage.addActor(scrollPane);
    stage.addActor(back);
    
    refresh();
  }

  @Override
  public void show() {
    stage.setScrollFocus(scrollPane);
  }
  
  public void refresh() {
    FileHandle fileHandle = Gdx.files.absolute(FileLogWriter.file.getAbsolutePath());
    String string = "";
    try {
      string = fileHandle.readString();
    } catch (GdxRuntimeException e) {
      Log.error("Failed to refresh log menu", e);
    }
    label.setText(string);
    resize(Graphics.GUI_WIDTH, Graphics.GUI_HEIGHT);
  }
  
  @Override
  public void resize(float width, float height) {
    super.resize(width, height);
    back.setBounds((width / 2) - (back.getMinWidth() / 2), 4, back.getMinWidth(), back.getMinHeight());
    scrollPane.setBounds(4, back.getHeight() + 8, width - 8, height - back.getHeight() - 12);
  }
}

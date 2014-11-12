package ethanjones.cubes.graphics.menu.menus;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;

public class InfoMenu extends Menu {

  protected Label text;
  protected TextButton button;

  public InfoMenu(String labelText, String buttonText) {
    super();
    text = new Label(labelText, new LabelStyle(Fonts.Size2, Color.WHITE));
    text.setAlignment(Align.center, Align.center);
    button = new TextButton(buttonText, skin);
  }

  public InfoMenu(String labelText, boolean back) {
    super();
    text = new Label(labelText, new LabelStyle(Fonts.Size2, Color.WHITE));
    text.setAlignment(Align.center, Align.center);
    if (back) button = MenuTools.getBackButton(this);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    if (button != null) {
      button.layout();
      text.layout();
      text.setBounds(0, button.getPrefHeight(), width, height - button.getPrefHeight());
      button.setBounds(width / 2 - button.getPrefWidth() / 2, height / 4, button.getPrefWidth(), button.getPrefHeight());
    } else {
      text.layout();
      text.setBounds(0, 0, width, height);
    }
  }

  @Override
  public void addActors() {
    stage.addActor(text);
    if (button != null) stage.addActor(button);
  }

  public boolean addButtonListener(EventListener listener) {
    if (button == null) return false;
    return button.addListener(listener);
  }
}

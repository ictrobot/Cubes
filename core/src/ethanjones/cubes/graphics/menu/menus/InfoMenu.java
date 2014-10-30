package ethanjones.cubes.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;

public class InfoMenu extends Menu {

  Label text;
  TextButton button;

  public InfoMenu(String labelText, String buttonText) {
    super();
    text = new Label(labelText, skin);
    button = new TextButton(buttonText, skin);
  }

  public InfoMenu(String labelText, boolean back) {
    super();
    text = new Label(labelText, skin);
    if (back) button = MenuTools.getBackButton(this);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    text.setBounds(width / 2 - text.getPrefWidth() / 2, height / 2, text.getPrefWidth(), text.getPrefHeight());
    if (button != null) {
      button.setBounds(width / 2 - button.getPrefWidth() / 2, height / 4, button.getPrefWidth(), button.getPrefHeight());
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

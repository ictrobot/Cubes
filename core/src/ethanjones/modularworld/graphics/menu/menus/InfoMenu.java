package ethanjones.modularworld.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.graphics.menu.MenuTools;

public class InfoMenu extends Menu {

  Label text;
  TextButton button;

  public InfoMenu(String labelText, String buttonText) {
    super();
    text = new Label(labelText, skin);
    button = new TextButton(buttonText, skin);
  }

  public InfoMenu(String labelText) {
    super();
    text = new Label(labelText, skin);
    button = MenuTools.getBackButton(this);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    
  }

  @Override
  public void addActors() {
    stage.addActor(text);
    stage.addActor(button);
  }
}

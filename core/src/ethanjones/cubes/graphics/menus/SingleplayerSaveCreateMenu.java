package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;
import ethanjones.cubes.world.client.ClientSaveManager;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SingleplayerSaveCreateMenu extends Menu {

  Label title;
  TextField name;
  TextButton start;
  TextButton back;

  public SingleplayerSaveCreateMenu() {
    super();
    title = new Label(Localization.get("menu.singleplayer.create.title"), skin.get("title", Label.LabelStyle.class));
    name = new TextField("", skin);
    name.setMessageText(Localization.get("menu.singleplayer.create.name"));
    name.setTextFieldFilter(new TextField.TextFieldFilter() {
      @Override
      public boolean acceptChar(TextField textField, char c) {
        return c >= 0x20 && c < 0x7F;
      }
    });
    start = new TextButton(Localization.get("menu.singleplayer.create.start"), skin);
    back = MenuTools.getBackButton(this);

    start.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(new SingleplayerLoadingMenu(ClientSaveManager.createSave(name.getText())));
      }
    });

    stage.addActor(title);
    stage.addActor(name);
    stage.addActor(start);
    stage.addActor(back);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    MenuTools.setTitle(title);
    MenuTools.arrange(width / 4, height / 2, width / 2, height / 4, MenuTools.Direction.Above, start, name);
    MenuTools.copyPosAndSize(start, back);
    back.setY(0);
  }
}

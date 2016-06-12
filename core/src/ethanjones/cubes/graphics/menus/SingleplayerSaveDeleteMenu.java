package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuManager;
import ethanjones.cubes.graphics.menu.MenuTools;
import ethanjones.cubes.world.client.ClientSaveManager;
import ethanjones.cubes.world.save.Save;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

public class SingleplayerSaveDeleteMenu extends Menu {

  Label title;
  Label text;
  TextButton delete;
  TextButton back;

  public SingleplayerSaveDeleteMenu(final Save save) {
    title = new Label(Localization.get("menu.singleplayer.delete.title"), skin.get("title", Label.LabelStyle.class));
    text = new Label(Localization.get("menu.singleplayer.delete.text", save.name), skin);
    delete = new TextButton(Localization.get("menu.singleplayer.delete.delete", save.name), skin);
    back = MenuTools.getBackButton(this);

    text.setAlignment(Align.center);

    delete.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        ClientSaveManager.deleteSave(save);
        Menu prev = MenuManager.getPrevious(SingleplayerSaveDeleteMenu.this);
        if (!(prev instanceof SingleplayerSavesMenu)) return;
        ((SingleplayerSavesMenu) prev).updateSavesList();
        Adapter.setMenu(prev);
      }
    });

    stage.addActor(title);
    stage.addActor(text);
    stage.addActor(delete);
    stage.addActor(back);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    MenuTools.setTitle(title);
    MenuTools.arrange(0, height / 2, width, height / 4, MenuTools.Direction.Above, delete, text);
    delete.setWidth(Math.max(delete.getPrefWidth(), back.getPrefWidth()));
    delete.setX((width / 2) - (delete.getWidth() / 2));
    MenuTools.copyPosAndSize(delete, back);
    back.setY(0);
  }

}

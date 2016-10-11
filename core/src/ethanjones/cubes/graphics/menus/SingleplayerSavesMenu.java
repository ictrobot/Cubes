package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;
import ethanjones.cubes.world.client.ClientSaveManager;
import ethanjones.cubes.world.save.Save;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SingleplayerSavesMenu extends Menu {

  Label title;
  ScrollPane scrollPane;
  List<Save> listLabel;
  TextButton back;
  TextButton delete;
  TextButton create;
  TextButton server;
  TextButton play;

  public SingleplayerSavesMenu() {
    title = new Label(Localization.get("menu.singleplayer.title"), skin.get("title", Label.LabelStyle.class));
    listLabel = new List<Save>(skin);

    scrollPane = new ScrollPane(listLabel, skin);
    scrollPane.setScrollingDisabled(true, false);

    back = MenuTools.getBackButton(this);

    updateSavesList();

    delete = new TextButton(Localization.get("menu.singleplayer.delete"), skin);
    delete.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Save save = listLabel.getSelected();
        if (save == null) return;
        Adapter.setMenu(new SingleplayerSaveDeleteMenu(save));
      }
    });

    create = new TextButton(Localization.get("menu.singleplayer.create"), skin);
    create.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(new SingleplayerSaveCreateMenu());
      }
    });

    server = new TextButton(Localization.get("menu.singleplayer.server_only"), skin);
    server.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Save save = listLabel.getSelected();
        if (save == null) return;
        Adapter.setMenu(new ServerSetupMenu(save));
      }
    });

    play = new TextButton(Localization.get("menu.singleplayer.play"), skin);
    play.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Save save = listLabel.getSelected();
        if (save == null) return;
        Adapter.setMenu(new SingleplayerLoadingMenu(save));
      }
    });

    stage.addActor(title);
    stage.addActor(scrollPane);
    stage.addActor(back);
    stage.addActor(delete);
    stage.addActor(create);
    stage.addActor(server);
    stage.addActor(play);
  }

  public void updateSavesList() {
    listLabel.setItems(ClientSaveManager.getSaves());
  }

  @Override
  public void resize(float width, float height) {
    super.resize(width, height);

    MenuTools.setTitle(title);

    MenuTools.setMaxPrefSize(back, delete, create, server, play);
    MenuTools.arrangeX(1f, false, back, delete, create, server, play);

    float w = listLabel.getPrefWidth();
    scrollPane.setBounds((width / 2) - (w / 2), back.getTop() + 1f, w, title.getY() - back.getTop() - 2f);
  }
}

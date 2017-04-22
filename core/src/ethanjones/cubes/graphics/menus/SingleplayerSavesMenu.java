package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.MenuTools;
import ethanjones.cubes.world.client.ClientSaveManager;
import ethanjones.cubes.world.save.Save;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SingleplayerSavesMenu extends Menu {
  
  Label title;
  Label noSaves;
  ScrollPane scrollPane;
  List<Save> listLabel;
  TextButton back;
  TextButton delete;
  TextButton create;
  TextButton play;
  
  public SingleplayerSavesMenu() {
    title = new Label(Localization.get("menu.singleplayer.title"), skin.get("title", Label.LabelStyle.class));
    noSaves = new Label(Localization.get("menu.singleplayer.nosave"), skin);
    
    listLabel = new List<Save>(skin);
    listLabel.addListener(new ActorGestureListener() {
      @Override
      public void tap(InputEvent event, float x, float y, int count, int button) {
        if (count == 2) play.toggle();
      }
    });
    
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
    stage.addActor(noSaves);
    stage.addActor(scrollPane);
    stage.addActor(back);
    stage.addActor(delete);
    stage.addActor(create);
    stage.addActor(play);
  }
  
  public void updateSavesList() {
    listLabel.setItems(ClientSaveManager.getSaves());
    if (listLabel.getItems().size > 0) {
      noSaves.setVisible(false);
      noSaves.toBack();
    } else {
      noSaves.setVisible(true);
      noSaves.toFront();
    }
  }
  
  @Override
  public void resize(float width, float height) {
    super.resize(width, height);
    
    MenuTools.setTitle(title);
    
    MenuTools.setMaxPrefSize(back, delete, create, play);
    MenuTools.arrangeX(1f, false, back, delete, create, play);
    
    float w = listLabel.getPrefWidth();
    scrollPane.setBounds((width / 2) - (w / 2), back.getTop() + 1f, w, title.getY() - back.getTop() - 2f);
    MenuTools.center(noSaves);
  }
}
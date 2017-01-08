package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.world.client.ClientSaveManager;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class SingleplayerTemporarySaveMenu extends SingleplayerSaveCreateMenu {
  
  public SingleplayerTemporarySaveMenu() {
    title.setText(Localization.get("menu.singleplayer.create.temporary_title"));
    name.setVisible(false);
    
    start.removeListener(startListener);
    start.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Adapter.setMenu(new SingleplayerLoadingMenu(ClientSaveManager.createTemporarySave(generator.getSelected().id, mode.getSelected(), seed.getText())));
      }
    });
  }
  
}

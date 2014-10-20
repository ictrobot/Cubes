package ethanjones.modularworld.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.graphics.menu.MenuTools;
import ethanjones.modularworld.side.common.ModularWorld;

public class MainMenu extends Menu {


  Label name;
  Table buttons;
  TextButton singleplayer;
  TextButton multiplayer;
  TextButton serveronly;
  TextButton quit;

  public MainMenu() {
    super();
    name = new Label(Branding.NAME, skin.get("title", Label.LabelStyle.class));
    buttons = new Table();
    buttons.addActor(singleplayer = new TextButton(Localization.get("menu.main.singleplayer"), skin));
    singleplayer.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new SingleplayerLoadingMenu());
        return true;
      }
    });
    buttons.addActor(quit = new TextButton(Localization.get("menu.main.quit"), skin));
    quit.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        Log.debug("Quit pressed");
        ModularWorld.quit(true);
        return true;
      }
    });
    buttons.addActor(multiplayer = new TextButton(Localization.get("menu.main.multiplayer"), skin));
    multiplayer.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new MultiplayerConnectMenu());
        return true;
      }
    });
    buttons.addActor(serveronly = new TextButton(Localization.get("menu.main.serveronly"), skin));
    serveronly.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setMenu(new ServerSetupMenu());
        return true;
      }
    });
  }

  @Override
  public void addActors() {
    stage.addActor(name);
    stage.addActor(buttons);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    MenuTools.setTitle(name);
    MenuTools.arrange(width / 4, height / 4, width / 2, height / 2, MenuTools.Direction.Above, quit, serveronly, multiplayer, singleplayer);
  }
}

package ethanjones.modularworld.graphics.menu;

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
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class MainMenu extends Menu {

  Label name;
  Table buttons;
  TextButton singleplayer;
  TextButton multiplayer;
  TextButton quit;

  public MainMenu() {
    super();
    name = new Label(Branding.NAME, skin);
    buttons = new Table();
    buttons.addActor(singleplayer = new TextButton(Localization.get("menu.main.single_player"), skin));
    singleplayer.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        GraphicalAdapter.instance.setModularWorld(
          new ModularWorldServer(new ServerNetworkingParameter()),
          new ModularWorldClient(new ClientNetworkingParameter("localhost"))
        );
        GraphicalAdapter.instance.setMenu(null);
        return true;
      }
    });
    buttons.addActor(quit = new TextButton(Localization.get("menu.main.quit"), skin));
    quit.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        Log.debug("MainMenu", "Quit pressed");
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
    //TODO Add button for Server only
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
    MenuTools.arrange(width / 4, height / 4, width / 2, height / 2, MenuTools.Direction.Above, quit, multiplayer, singleplayer);
    MenuTools.fitText(singleplayer, multiplayer, quit);
  }
}

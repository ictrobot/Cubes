package ethanjones.cubes.graphics.menu.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.side.server.CubesServer;

public class ServerRunningMenu extends InfoMenu {

  private final int port;
  private int frameNum = 0;

  public ServerRunningMenu(int port) {
    super(Localization.get("menu.general.loading"), Localization.get("menu.server.stop"));
    this.port = port;

    addButtonListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        Adapter.gotoMainMenu();
        return true;
      }
    });
  }

  public void render() {
    super.render();
    frameNum++;
    if (frameNum != 2) return;
    try {
      NetworkingManager.serverPreInit(new ServerNetworkingParameter(port));
    } catch (Exception e) {
      Log.error("Failed to start server", e);
      Adapter.setMenu(new ConnectionFailedMenu(e));
      Adapter.setClient(null);
      Adapter.setServer(null);
      return;
    }
    CubesServer cubesServer = new CubesServer();
    Adapter.setServer(cubesServer);
    Adapter.setClient(null);
    text.setText(Localization.get("menu.server.running"));
    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }
}

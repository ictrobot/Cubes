package ethanjones.cubes.client.graphics.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ethanjones.cubes.common.localization.Localization;
import ethanjones.cubes.common.logging.Log;
import ethanjones.cubes.platform.Adapter;
import ethanjones.cubes.common.networking.NetworkingManager;
import ethanjones.cubes.common.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.server.integrated.ServerOnlyServer;

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
      Adapter.setServer(new ServerOnlyServer());
      Adapter.setClient(null);
    } catch (Exception e) {
      Log.error("Failed to start server", e);
      Adapter.setMenu(new ConnectionFailedMenu(e));
      Adapter.setClient(null);
      Adapter.setServer(null);
      return;
    }
    text.setText(Localization.get("menu.server.running"));
    resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
  }
}

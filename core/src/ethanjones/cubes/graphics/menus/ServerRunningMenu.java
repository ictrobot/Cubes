package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.StopLoopException;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.side.server.integrated.ServerOnlyServer;
import ethanjones.cubes.world.save.Save;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import static ethanjones.cubes.graphics.Graphics.GUI_HEIGHT;
import static ethanjones.cubes.graphics.Graphics.GUI_WIDTH;

public class ServerRunningMenu extends InfoMenu {

  private final Save save;
  private final int port;
  private int frameNum = 0;

  public ServerRunningMenu(Save save, int port) {
    super(Localization.get("menu.general.loading"), Localization.get("menu.server.stop"));
    this.save = save;
    this.port = port;

    addButtonListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        try {
          Adapter.gotoMainMenu();
        } catch (StopLoopException ignored) {
        }
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
      Adapter.setServer(new ServerOnlyServer(save));
      Adapter.setClient(null);
    } catch (Exception e) {
      Log.error("Failed to start server", e);
      Adapter.setMenu(new ConnectionFailedMenu(e));
      Adapter.setClient(null);
      Adapter.setServer(null);
      return;
    }
    text.setText(Localization.get("menu.server.running"));
    resize(GUI_WIDTH, GUI_HEIGHT);
  }
}

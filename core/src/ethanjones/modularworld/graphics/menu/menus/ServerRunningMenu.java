package ethanjones.modularworld.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class ServerRunningMenu extends InfoMenu {

  private final int port;
  private int frameNum = 0;

  public ServerRunningMenu(int port) {
    super(Localization.get("menu.server.running"), Localization.get("menu.server.stop"));
    this.port = port;

    addButtonListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        ModularWorld.quit(false);
        return true;
      }
    });
  }

  public void render() {
    super.render();
    frameNum++;
    if (frameNum != 2) return;
    ModularWorldServer modularWorldServer = new ModularWorldServer(new ServerNetworkingParameter(port));
    GraphicalAdapter.instance.setModularWorld(
      modularWorldServer,
      null
    );
  }
}

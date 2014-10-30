package ethanjones.cubes.graphics.menu.menus;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import ethanjones.cubes.core.adapter.GraphicalAdapter;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.CubesServer;

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
        Cubes.quit(false);
        return true;
      }
    });
  }

  public void render() {
    super.render();
    frameNum++;
    if (frameNum != 2) return;
    CubesServer cubesServer = new CubesServer(new ServerNetworkingParameter(port));
    GraphicalAdapter.instance.setCubes(cubesServer, null);
  }
}

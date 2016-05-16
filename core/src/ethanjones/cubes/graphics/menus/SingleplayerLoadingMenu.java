package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.integrated.SingleplayerServer;

public class SingleplayerLoadingMenu extends InfoMenu {

  private int frameNum = 0;

  public SingleplayerLoadingMenu() {
    super(Localization.get("menu.general.loading"), false);
  }

  @Override
  public void render() {
    super.render();
    frameNum++;
    if (frameNum != 2) return;
    try {
      try {
        NetworkingManager.singleplayerPreInit();
        Adapter.setServer(new SingleplayerServer());
        Adapter.setClient(new CubesClient());
        Adapter.setMenu(new WorldLoadingMenu());
      } catch (Exception e) {
        Log.error("Failed to setup client", e);
      }
    } catch (Exception e) {
      Log.error("Failed to start singleplayer", e);
      Adapter.setMenu(new ConnectionFailedMenu(e));
      Adapter.setClient(null);
      Adapter.setServer(null);
    }
  }
}

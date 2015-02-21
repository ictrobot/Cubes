package ethanjones.cubes.client.graphics.menus;

import ethanjones.cubes.common.localization.Localization;
import ethanjones.cubes.common.logging.Log;
import ethanjones.cubes.platform.Adapter;
import ethanjones.cubes.common.networking.NetworkingManager;
import ethanjones.cubes.client.CubesClient;
import ethanjones.cubes.server.integrated.SingleplayerServer;

public class SingleplayerLoadingMenu extends InfoMenu {

  public SingleplayerLoadingMenu() {
    super(Localization.get("menu.general.loading"), false);
  }

  public void render() {
    super.render();
    try {
      try {
        NetworkingManager.singleplayerPreInit();
        Adapter.setServer(new SingleplayerServer());
        Adapter.setClient(new CubesClient());
        Adapter.setMenu(null);
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

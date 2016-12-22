package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.graphics.menus.ClientErrorMenu.FailedStartingSingleplayerMenu;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.integrated.SingleplayerServer;
import ethanjones.cubes.world.save.Save;

public class SingleplayerLoadingMenu extends InfoMenu {

  private final Save save;
  private int frameNum = 0;

  public SingleplayerLoadingMenu(Save save) {
    super(Localization.get("menu.general.loading"), false);
    this.save = save;
  }

  @Override
  public void render() {
    super.render();
    frameNum++;
    if (frameNum != 2) return;
    try {
      try {
        NetworkingManager.singleplayerPreInit();
        Adapter.setServer(new SingleplayerServer(save));
        Adapter.setClient(new CubesClient());
        Adapter.setMenu(new WorldLoadingMenu());
      } catch (Exception e) {
        Log.error("Failed to setup client", e);
      }
    } catch (Exception e) {
      Log.error("Failed to start singleplayer", e);
      Adapter.setMenu(new FailedStartingSingleplayerMenu(e));
      Adapter.setClient(null);
      Adapter.setServer(null);
    }
  }
}

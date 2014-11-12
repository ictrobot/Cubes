package ethanjones.cubes.graphics.menu.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.client.ClientNetworkingParameter;
import ethanjones.cubes.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;

public class SingleplayerLoadingMenu extends InfoMenu {

  public SingleplayerLoadingMenu() {
    super(Localization.get("menu.general.loading"), false);
  }

  public void render() {
    super.render();
    try {
      NetworkingManager.serverPreInit(new ServerNetworkingParameter());
      NetworkingManager.clientPreInit(new ClientNetworkingParameter("localhost", Settings.getIntegerSettingValue(Settings.NETWORKING_PORT)));
    } catch (Exception e) { //FIXME
      Log.error("Failed to open singleplayer", e);
    }
    Adapter.setServer(new CubesServer());
    Adapter.setClient(new CubesClient());
    Adapter.setMenu(null);
  }
}

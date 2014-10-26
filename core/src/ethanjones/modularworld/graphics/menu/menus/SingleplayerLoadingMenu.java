package ethanjones.modularworld.graphics.menu.menus;

import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.ModularWorldServer;

public class SingleplayerLoadingMenu extends InfoMenu {
  public SingleplayerLoadingMenu() {
    super(Localization.get("menu.general.loading"), false);
  }

  public void render() {
    super.render();
    GraphicalAdapter.instance.setModularWorld(
      new ModularWorldServer(new ServerNetworkingParameter()),
      new ModularWorldClient(new ClientNetworkingParameter("localhost", Settings.getIntegerSettingValue(Settings.NETWORKING_PORT)))
    );
    GraphicalAdapter.instance.setMenu(null);
  }
}

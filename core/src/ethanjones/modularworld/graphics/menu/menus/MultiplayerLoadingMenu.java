package ethanjones.modularworld.graphics.menu.menus;

import com.badlogic.gdx.utils.GdxRuntimeException;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.side.client.ModularWorldClient;

public class MultiplayerLoadingMenu extends InfoMenu {

  private final String address;
  private final int port;

  public MultiplayerLoadingMenu(String address, int port) {
    super(Localization.get("menu.multiplayer_connect.connecting"), false);
    this.address = address;
    this.port = port;
  }

  public void render() {
    super.render(); //TODO fix not showing connecting
    try {
      ModularWorldClient modularWorldClient = new ModularWorldClient(new ClientNetworkingParameter(address, port));
      GraphicalAdapter.instance.setModularWorld(
        null,
        modularWorldClient
      );
      GraphicalAdapter.instance.setMenu(null);
    } catch (Exception e) {
      if (e instanceof GdxRuntimeException && e.getCause() instanceof Exception) e = (Exception) e.getCause();
      GraphicalAdapter.instance.setMenu(new MultiplayerFailedMenu(e));
    }
  }
}

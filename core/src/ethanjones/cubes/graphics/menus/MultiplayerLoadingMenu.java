package ethanjones.cubes.graphics.menus;

import com.badlogic.gdx.utils.GdxRuntimeException;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.client.ClientNetworkingParameter;
import ethanjones.cubes.side.client.CubesClient;

public class MultiplayerLoadingMenu extends InfoMenu {

  private final String address;
  private final int port;
  private int frameNum = 0;

  public MultiplayerLoadingMenu(String address, int port) {
    super(Localization.get("menu.multiplayer.connecting"), false);
    this.address = address;
    this.port = port;
  }

  public void render() {
    super.render();
    frameNum++;
    if (frameNum != 2) return;
    try {
      NetworkingManager.clientPreInit(new ClientNetworkingParameter(address, port));
      CubesClient cubesClient = new CubesClient();
      Adapter.setServer(null);
      Adapter.setClient(cubesClient);
      Adapter.setMenu(null);
    } catch (Exception e) {
      if (e instanceof GdxRuntimeException && e.getCause() instanceof Exception) e = (Exception) e.getCause();
      Adapter.setClient(null);
      Log.error("Failed to connect", e);
      Log.error("Address:" + address + " Port:" + port);
      Adapter.setMenu(new ConnectionFailedMenu(e));
      Adapter.setClient(null);
      Adapter.setServer(null);
    }
  }
}

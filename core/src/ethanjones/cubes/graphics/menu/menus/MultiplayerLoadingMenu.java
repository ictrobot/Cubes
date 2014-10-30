package ethanjones.cubes.graphics.menu.menus;

import com.badlogic.gdx.utils.GdxRuntimeException;

import ethanjones.cubes.core.adapter.GraphicalAdapter;
import ethanjones.cubes.core.localization.Localization;
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
      CubesClient cubesClient = new CubesClient(new ClientNetworkingParameter(address, port));
      GraphicalAdapter.instance.setCubes(null, cubesClient);
      GraphicalAdapter.instance.setMenu(null);
    } catch (Exception e) {
      if (e instanceof GdxRuntimeException && e.getCause() instanceof Exception) e = (Exception) e.getCause();
      GraphicalAdapter.instance.setMenu(new MultiplayerFailedMenu(e));
    }
  }
}

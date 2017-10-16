package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menus.MainMenu;
import ethanjones.cubes.graphics.menus.RunnableMenu;
import ethanjones.cubes.input.InputChain;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.CubesServer;

import com.badlogic.gdx.utils.reflect.ClassReflection;

public class Adapter {

  private static AdapterInterface adapter;

  public static void setClient(CubesClient cubesClient) throws UnsupportedOperationException {
    adapter.setClient(cubesClient);
  }

  public static void setServer(CubesServer cubesServer) throws UnsupportedOperationException {
    adapter.setServer(cubesServer);
  }

  public static Menu getMenu() {
    return adapter.getMenu();
  }

  public static void setMenu(Menu menu) throws UnsupportedOperationException {
    adapter.setMenu(menu);
  }

  public static void dispose() {
    Log.debug("Disposing adapter");
    final Menu menu = adapter.getMenu();
    try {
      if (menu != null) {
        menu.hide();
        InputChain.hideMenu(menu);
      }
    } catch (Exception e) {
      Debug.crash(e);
    }
    stop();
  }

  private static void stop() {
    final CubesClient cubesClient = adapter.getClient();
    final CubesServer cubesServer = adapter.getServer();

    try {
      if (cubesServer != null) {
        cubesServer.dispose();
      }
      if (cubesClient != null) {
        cubesClient.dispose();
      }
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  public static boolean isDedicatedServer() {
    return adapter.getSide() == Side.Server;
  }

  public static AdapterInterface getInterface() {
    return adapter;
  }

  public static void setInterface(AdapterInterface adapterInterface) {
    if (adapter == null && adapterInterface != null) adapter = adapterInterface;
  }

  public static void gotoMainMenu() {
    gotoMenu(new MainMenu());
  }
  
  
  /**
   * Will exit if server
   */
  public static void gotoMenu(final Menu menu) {
    if (isDedicatedServer()) quit();
    if (menu == null || adapter.getMenu() instanceof RunnableMenu || ClassReflection.isInstance(menu.getClass(), adapter.getMenu())) return;
  
    adapter.setMenu(new RunnableMenu(new Runnable() {
      @Override
      public void run() {
        if (adapter.getClient() == null && adapter.getServer() == null) {
          adapter.setMenu(menu);
        }
      }
    }));
    stop();
  }

  public static void quit() {
    Compatibility.get()._exit(0); // Force android to exit vm
    //Gdx.app.exit();
  }
}

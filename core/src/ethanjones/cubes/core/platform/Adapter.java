package ethanjones.cubes.core.platform;

import com.badlogic.gdx.Gdx;

import ethanjones.cubes.core.messaging.MessageManager;
import ethanjones.cubes.graphics.gui.Menu;
import ethanjones.cubes.graphics.menu.WaitingMenu;
import ethanjones.cubes.graphics.menu.WaitingMenu.Callback;
import ethanjones.cubes.side.ControlMessage;
import ethanjones.cubes.side.ControlMessage.Status;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;

public class Adapter {

  private static AdapterInterface adapter;
  private static boolean mainMenu = false;

  public static void setInterface(AdapterInterface adapterInterface) {
    if (adapter == null && adapterInterface != null) adapter = adapterInterface;
  }

  public static AdapterInterface getInterface() {
    return adapter;
  }

  public static void quit() {
    Gdx.app.exit();
  }

  public static void setClient(CubesClient cubesClient) throws UnsupportedOperationException {
    adapter.setClient(cubesClient);
  }

  public static void setServer(CubesServer cubesServer) throws UnsupportedOperationException {
    adapter.setServer(cubesServer);
  }

  public static void setMenu(Menu menu) throws UnsupportedOperationException {
    adapter.setMenu(menu);
  }

  public static Menu getMenu() {
    return adapter.getMenu();
  }

  /**
   * Will exit if server
   */
  public static void gotoMainMenu() {
    if (Compatibility.get().isServer()) quit();
    if (mainMenu) return;
    mainMenu = true;
    WaitingMenu waitingMenu = new WaitingMenu(new Callback() {
      @Override
      public void callback() {
        mainMenu = false;
      }
    });

    if (adapter.getServer() != null) {
      waitingMenu.addObject(adapter.getServer());
      ControlMessage controlMessage = new ControlMessage();
      controlMessage.status = Status.Stop;
      controlMessage.from = waitingMenu;
      MessageManager.sendMessage(controlMessage, adapter.getServer());
    }

    if (adapter.getClient() != null) {
      waitingMenu.addObject(adapter.getClient());
      ControlMessage controlMessage = new ControlMessage();
      controlMessage.status = Status.Stop;
      controlMessage.from = waitingMenu;
      MessageManager.sendMessage(controlMessage, adapter.getClient());
    }

    setMenu(waitingMenu);
  }
  
}

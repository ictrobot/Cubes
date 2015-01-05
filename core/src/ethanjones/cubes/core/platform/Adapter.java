package ethanjones.cubes.core.platform;

import com.badlogic.gdx.Gdx;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menus.MainMenu;
import ethanjones.cubes.graphics.menus.WaitingMenu;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;

public class Adapter {

  private static AdapterInterface adapter;

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

  public static void dispose() {
    Log.debug("Disposing adapter");
    final Menu menu = adapter.getMenu();
    try {
      if (menu != null) {
        menu.hide();
      }
    } catch (Exception e) {
      Debug.crash(e);
    }
    stop();
  }

  /**
   * Will exit if server
   */
  public static void gotoMainMenu() {
    if (Compatibility.get().isServer()) quit();
    if (adapter.getMenu() instanceof WaitingMenu || adapter.getMenu() instanceof MainMenu) return;

    adapter.setMenu(new WaitingMenu(new Runnable() {
      @Override
      public void run() {
        if (adapter.getClient() == null && adapter.getServer() == null) {
          adapter.setMenu(new MainMenu());
        }
      }
    }));
    stop();
  }

  private static void stop() {
    final CubesClient cubesClient = adapter.getClient();
    final CubesServer cubesServer = adapter.getServer();
    final Thread currentThread = Thread.currentThread();

    if (!Compatibility.get().isServer()) {
      if (Adapter.getInterface().getThread() == currentThread) {
        stopFromClientThread(cubesClient, cubesServer);
      } else if (cubesServer != null && cubesServer.getThread() == currentThread) {
        stopFromServerThread(cubesClient, cubesServer);
      } else {
        stopFromOtherThread(cubesClient, cubesServer);
      }
    } else {
      if (Adapter.getInterface().getThread() == currentThread) {
        stopFromServerThread(cubesClient, cubesServer);
      } else {
        stopFromOtherThread(cubesClient, cubesServer);
      }
    }
  }

  private static void stopFromClientThread(final CubesClient cubesClient, final CubesServer cubesServer) {
    try {
      if (cubesServer != null) {
        cubesServer.dispose();
      }
      if (cubesClient != null) {
        cubesClient.dispose();
      }
      if (cubesServer != null) {
        try {
          cubesServer.getThread().join(1000);
        } catch (InterruptedException e) {
        }
        if (cubesServer.getThread().isAlive()) {
          failedToStopThread(cubesServer.getThread());
        }
      }
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  private static void stopFromServerThread(final CubesClient cubesClient, final CubesServer cubesServer) {
    try {
      if (cubesClient != null) {
        cubesClient.dispose();
      }
      if (cubesServer != null) {
        cubesServer.dispose();
      }
      if (cubesClient != null) {
        try {
          cubesClient.getThread().join(1000);
        } catch (InterruptedException e) {
        }
        if (cubesClient.getThread().isAlive()) {
          failedToStopThread(cubesClient.getThread());
        }
      }
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  private static void stopFromOtherThread(final CubesClient cubesClient, final CubesServer cubesServer) {
    try {
      if (cubesClient != null) {
        cubesClient.dispose();
      }
      if (cubesServer != null) {
        cubesServer.dispose();
      }
      if (cubesClient != null) {
        try {
          cubesClient.getThread().join(1000);
        } catch (InterruptedException e) {
        }
        if (cubesClient.getThread().isAlive()) {
          failedToStopThread(cubesClient.getThread());
        }
      }
      if (cubesServer != null) {
        try {
          cubesServer.getThread().join(1000);
        } catch (InterruptedException e) {
        }
        if (cubesServer.getThread().isAlive()) {
          failedToStopThread(cubesServer.getThread());
        }
      }
    } catch (Exception e) {
      Debug.crash(e);
    }
  }

  private static synchronized void failedToStopThread(Thread thread) {
    StackTraceElement[] stackTrace = thread.getStackTrace();
    Log.error("Failed to stop " + thread.getName() + " thread");
    for (StackTraceElement stackTraceElement : stackTrace) {
      Log.error("  " + stackTraceElement.toString());
    }
  }
}

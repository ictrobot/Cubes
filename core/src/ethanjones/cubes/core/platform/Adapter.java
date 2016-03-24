package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menus.MainMenu;
import ethanjones.cubes.graphics.menus.RunnableMenu;
import ethanjones.cubes.input.InputChain;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;

import com.badlogic.gdx.Gdx;

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
        menu.save();
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
    final Thread currentThread = Thread.currentThread();

    if (!isDedicatedServer()) {
      if (Adapter.getInterface().getThread() == currentThread) {
        stopFromClientThread(cubesClient, cubesServer);
      } else if (cubesServer != null && cubesServer.getThread() == currentThread) {
        stopFromServerThread(cubesClient, cubesServer);
      } else {
        stopFromOtherThread(cubesClient, cubesServer);
      }
      throw new StopLoopException();
    } else {
      if (Adapter.getInterface().getThread() == currentThread) {
        stopFromServerThread(cubesClient, cubesServer);
      } else {
        stopFromOtherThread(cubesClient, cubesServer);
      }
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

  /**
   * Will exit if server
   */
  public static void gotoMainMenu() {
    if (isDedicatedServer()) quit();
    if (adapter.getMenu() instanceof RunnableMenu || adapter.getMenu() instanceof MainMenu) return;

    adapter.setMenu(new RunnableMenu(new Runnable() {
      @Override
      public void run() {
        if (adapter.getClient() == null && adapter.getServer() == null) {
          adapter.setMenu(new MainMenu());
        }
      }
    }));
    stop();
  }

  public static void quit() {
    Gdx.app.exit();
  }
}

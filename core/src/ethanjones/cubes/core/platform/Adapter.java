package ethanjones.cubes.core.platform;

import ethanjones.cubes.core.event.core.InstanceChangedEvent;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menus.MainMenu;
import ethanjones.cubes.graphics.menus.RunnableMenu;
import ethanjones.cubes.input.InputChain;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.world.thread.WorldTasks;

import com.badlogic.gdx.Gdx;

public class Adapter {

  private static final int JOIN_TIMEOUT = 60000;
  private static AdapterInterface adapter;

  public static void setClient(CubesClient cubesClient) throws UnsupportedOperationException {
    adapter.setClient(cubesClient);
    new InstanceChangedEvent.ClientChangedEvent().post();
  }

  public static void setServer(CubesServer cubesServer) throws UnsupportedOperationException {
    adapter.setServer(cubesServer);
    new InstanceChangedEvent.ServerChangedEvent().post();
  }

  public static Menu getMenu() {
    return adapter.getMenu();
  }

  public static void setMenu(Menu menu) throws UnsupportedOperationException {
    adapter.setMenu(menu);
    new InstanceChangedEvent.MenuChangedEvent().post();
  }

  public static void dispose() {
    Log.debug("Disposing adapter");
    Gdx.app.postRunnable(new Runnable() {
      @Override
      public void run() {
        Gdx.app.exit();
      }
    });
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
    stopBackground();
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
          cubesServer.getThread().join(JOIN_TIMEOUT);
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
//      if (cubesClient != null) {
//        try {
//          cubesClient.getThread().join(JOIN_TIMEOUT);
//        } catch (InterruptedException e) {
//        }
//        if (cubesClient.getThread().isAlive()) {
//          failedToStopThread(cubesClient.getThread());
//        }
//      }
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
//      if (cubesClient != null) {
//        try {
//          cubesClient.getThread().join(JOIN_TIMEOUT);
//        } catch (InterruptedException e) {
//        }
//        if (cubesClient.getThread().isAlive()) {
//          failedToStopThread(cubesClient.getThread());
//        }
//      }
//      if (cubesServer != null) {
//        try {
//          cubesServer.getThread().join(JOIN_TIMEOUT);
//        } catch (InterruptedException e) {
//        }
//        if (cubesServer.getThread().isAlive()) {
//          failedToStopThread(cubesServer.getThread());
//        }
//      }
    } catch (Exception e) {
      Debug.crash(e);
    }
  }
  
  private static void stopBackground() {
    try {
      Executor.stop();
      WorldTasks.dispose();
    } catch (Exception ignored) {
    }
  }

  private static synchronized void failedToStopThread(Thread thread) {
    StackTraceElement[] stackTrace = thread.getStackTrace();
    Log.error("Failed to stop " + thread.getName() + " thread");
    for (StackTraceElement stackTraceElement : stackTrace) {
      Log.error("  " + stackTraceElement.toString());
    }
  }

  public static void gotoMainMenu() {
    gotoMenu(new MainMenu());
  }
  
  
  /**
   * Will exit if server
   */
  public static void gotoMenu(final Menu menu) {
    if (isDedicatedServer()) quit();
    if (menu == null || adapter.getMenu() instanceof RunnableMenu || menu.getClass().isInstance(adapter.getMenu())) return;
  
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
    System.exit(0); // Force android to exit vm
    //Gdx.app.exit();
  }
}

package ethanjones.cubes.side.common;

import com.badlogic.gdx.Gdx;

import ethanjones.cubes.core.adapter.GraphicalAdapter;
import ethanjones.cubes.core.compatibility.Compatibility;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.system.Threads;
import ethanjones.cubes.core.timing.TimeHandler;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menu.menus.MainMenu;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.SimpleApplication;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.world.World;

public abstract class Cubes implements SimpleApplication, TimeHandler {

  public static final int tickMS = 25;
  private static boolean setup;

  public static void setup() {
    if (setup) return;
    if (Compatibility.get() == null) {
      Log.error(new CubesException("No Compatibility module for this platform: " + Gdx.app.getType().name() + ", OS: " + System.getProperty("os.name") + ", Arch:" + System.getProperty("os.arch")));
    }

    Compatibility.get().getBaseFolder().mkdirs();

    Log.info(Branding.DEBUG); //Can't log till base folder setup
    Debug.printProperties();

    Sided.setupGlobal();

    Compatibility.get().init(null);
    Compatibility.get().logEnvironment();

    Settings.init();
    Threads.init();

    Assets.preInit();
    ModManager.init();
    Assets.init();
    Localization.load();

    Settings.print();

    setup = true;
  }

  /**
   * Always exits if is headless
   */
  public static synchronized final void quit(boolean exit) {
    if (CubesServer.instance != null) {
      if (CubesServer.instance.thread != null) {
        CubesServer.instance.thread.dispose();
        try {
          CubesServer.instance.thread.join(10000); //Wait for 10 seconds
        } catch (InterruptedException e) {

        }
      } else {
        CubesServer.instance.dispose();
      }
    }

    if (CubesClient.instance != null) {
      CubesClient.instance.dispose();
    }

    if (exit || Compatibility.get().isHeadless()) {
      staticDispose();
      System.exit(0);
    } else {
      GraphicalAdapter.instance.setCubes(null, null);
      GraphicalAdapter.instance.setMenu(new MainMenu());
    }

  }

  protected static void staticDispose() {
    Threads.disposeExecutor();
    Menu.staticDispose();
  }

  private final Side side;
  public World world;

  public Cubes(Side side) {
    this.side = side;
  }

  @Override
  public void create() {
    Sided.setup(side);
    Compatibility.get().init(side);
    Sided.getEventBus().register(this);
    Sided.getTiming().addHandler(this, tickMS);
  }

  @Override
  public void render() {
    NetworkingManager.getNetworking(side).processPackets();
    Sided.getTiming().update();
  }

  @Override
  public void dispose() {
    write();
    NetworkingManager.getNetworking(side).stop();
    world.dispose();
    Sided.reset(side);
  }

  public void write() {
    Settings.write();
  }

  public void time(int interval) {
    if (interval == tickMS) tick();
  }

  public void tick() {
    NetworkingManager.getNetworking(side).tick();
  }
}

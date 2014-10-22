package ethanjones.modularworld.side.common;

import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.mod.ModManager;
import ethanjones.modularworld.core.system.Branding;
import ethanjones.modularworld.core.system.Debug;
import ethanjones.modularworld.core.system.ModularWorldException;
import ethanjones.modularworld.core.system.Threads;
import ethanjones.modularworld.core.timing.TimeHandler;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.Sided;
import ethanjones.modularworld.side.SimpleApplication;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.ModularWorldServer;
import ethanjones.modularworld.world.World;

public abstract class ModularWorld implements SimpleApplication, TimeHandler {

  private static final int tickMS = 16;
  private static boolean setup;
  private final Side side;
  public World world;

  public ModularWorld(Side side) {
    this.side = side;
  }

  public static void setup() {
    if (setup) return;
    if (Compatibility.get() == null)
      Log.error(new ModularWorldException("No Compatibility module for this platform: " + Gdx.app.getType().name() + ", OS: " + System.getProperty("os.name") + ", Arch:" + System.getProperty("os.arch")));

    Compatibility.get().getBaseFolder().mkdirs();

    Log.info(Branding.DEBUG); //Can't log till base folder setup
    Debug.printProperties();

    Sided.setupGlobal();
    Compatibility.get().init(null);
    Compatibility.get().logEnvironment();

    Threads.init();

    Localization.load(Sided.getAssetManager().assets);

    ModManager.init();

    setup = true;
  }

  protected static void staticDispose() {
    Threads.disposeExecutor();
    Menu.staticDispose();
  }

  /**
   * Always exits if is headless
   */
  public static final void quit(boolean exit) {
    if (ModularWorldClient.instance != null) {
      ModularWorldClient.instance.dispose();
    }
    if (ModularWorldServer.instance != null) {
      ModularWorldServer.instance.dispose();
    }
    if (exit || Compatibility.get().isHeadless()) {
      staticDispose();
      System.exit(0);
    } else {
      GraphicalAdapter.instance.gotoMainMenu();
    }
  }

  @Override
  public void create() {
    //TODO Rewrite settings, have two classes "Client" and "Server"
    Sided.setup(side);
    Compatibility.get().init(side);
    Sided.getEventBus().register(this);
    Sided.getTiming().addHandler(this, tickMS);
  }

  @Override
  public void render() {
    NetworkingManager.getNetworking(side).processPackets();
  }

  public void tick() {
    NetworkingManager.getNetworking(side).tick();
  }

  public void write() {
    Sided.getSettingsManager().writeToFile();
  }

  @Override
  public void dispose() {
    write();
    NetworkingManager.getNetworking(side).stop();
    world.dispose();
    Sided.reset(side);
  }

  public void time(int interval) {
    if (interval == tickMS) tick();
  }
}

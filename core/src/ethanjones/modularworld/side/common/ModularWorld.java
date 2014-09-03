package ethanjones.modularworld.side.common;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.block.BlockManager;
import ethanjones.modularworld.block.Blocks;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.core.debug.Memory;
import ethanjones.modularworld.core.events.EventBus;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.mod.ModManager;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.core.settings.SettingsManager;
import ethanjones.modularworld.core.thread.Threads;
import ethanjones.modularworld.core.timing.TimeHandler;
import ethanjones.modularworld.core.timing.Timing;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.asset.AssetManager;
import ethanjones.modularworld.graphics.menu.Menu;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.server.ModularWorldServer;
import ethanjones.modularworld.world.World;

public abstract class ModularWorld implements ApplicationListener, TimeHandler {

  private static final int tickMS = 16;
  public static Compatibility compatibility;
  public static AssetManager assetManager;
  public static FileHandle baseFolder;
  public static EventBus eventBus;
  public static SettingsManager settings;
  public static BlockManager blockManager;
  public static Timing timing;
  private static boolean setup;
  private final Side side;
  public World world;

  public ModularWorld(Side side) {
    this.side = side;
  }

  public static void setup() {
    if (setup) return;
    if (compatibility == null)
      Log.error(new ModularWorldException("No Compatibility module for this platform: " + Gdx.app.getType().name() + ", OS: " + System.getProperty("os.name") + ", Arch:" + System.getProperty("os.arch")));

    eventBus = new EventBus();
    compatibility.init();

    baseFolder = compatibility.getBaseFolder();
    baseFolder.mkdirs();

    Log.info(Branding.DEBUG); //Can't log till base folder setup

    compatibility.logEnvironment();
    Debug.printProperties();

    settings = new SettingsManager();
    Settings.processAll();
    settings.readFromFile();
    settings.print();

    assetManager = new AssetManager();
    blockManager = new BlockManager();

    Blocks.init();

    timing = new Timing();

    Threads.init();

    AssetManager assetManager = new AssetManager();
    compatibility.getAssets(assetManager);

    if (!compatibility.isHeadless()) {
      GraphicsHelper.init(assetManager);
    }

    Localization.load(assetManager.assets);

    ModManager.init();

    setup = true;
  }

  public static void staticRender() {
    Memory.update();
    timing.update();
  }

  protected static void staticDispose() {
    Threads.disposeExecutor();
    Menu.staticDispose();
  }

  protected static void sharedDispose() {
    timing.dispose();
  }

  /**
   * Always exits if is headless
   */
  public static final void quit(boolean exit) {
    sharedDispose();
    if (ModularWorldClient.instance != null) {
      ModularWorldClient.instance.dispose();
    }
    if (ModularWorldServer.instance != null) {
      ModularWorldServer.instance.dispose();
    }
    if (exit || compatibility.isHeadless()) {
      staticDispose();
      System.exit(0);
    } else {
      GraphicalAdapter.instance.gotoMainMenu();
    }
  }

  @Override
  public void create() {
    //TODO Rewrite settings, have two classes "Client" and "Server"
    eventBus.register(this);
    timing.addHandler(this, tickMS);
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void render() {
    NetworkingManager.getNetworking(side).processPackets();
  }

  public void tick() {
    NetworkingManager.getNetworking(side).tick();
  }

  public void write() {
    settings.writeToFile();
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }

  @Override
  public void dispose() {
    write();
    NetworkingManager.getNetworking(side).stop();
    world.dispose();
  }

  public void time(int interval) {
    if (interval == tickMS) tick();
  }
}

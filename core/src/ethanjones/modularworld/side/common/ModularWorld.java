package ethanjones.modularworld.side.common;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.block.factory.BlockFactories;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.events.EventBus;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.core.settings.SettingsManager;
import ethanjones.modularworld.core.thread.Threads;
import ethanjones.modularworld.core.timing.Timing;
import ethanjones.modularworld.graphics.asset.AssetManager;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.side.client.debug.Debug;
import ethanjones.modularworld.world.World;

public abstract class ModularWorld implements ApplicationListener {

  public static Compatibility compatibility; //
  public static AssetManager assetManager;
  public static FileHandle baseFolder; //
  public static EventBus eventBus; //
  public static SettingsManager settings;
  public static Timing timing;
  private static boolean setup;

  public static void setup() {
    if (setup) return;
    ModularWorld.compatibility = compatibility;
    eventBus = new EventBus();
    compatibility.init();

    baseFolder = compatibility.getBaseFolder();
    baseFolder.mkdirs();
    Log.info(baseFolder.path());

    settings = new SettingsManager();
    Settings.processAll();
    settings.readFromFile();
    settings.print();

    assetManager = new AssetManager();

    timing = new Timing();
    setup = true;
  }

  public World world;

  @Override
  public void create() {
    //TODO: Rewrite settings, have two classes "Client" and "Server"
    if (compatibility == null) {
      Log.error(new ModularWorldException("No Compatibility module for this platform: " + Gdx.app.getType().name() + ", OS: " + System.getProperty("os.name") + ", Arch:" + System.getProperty("os.arch")));
    }
    setup();
    eventBus.register(this);

    Log.info(Branding.NAME, Branding.DEBUG);
    compatibility.logEnvironment();
    Debug.printProperties();

    NetworkingManager.readPort();

    Threads.init();

    BlockFactories.init();
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void render() {
    timing.update();
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
    NetworkingManager.stop();
    Threads.disposeExecutor();
    world.dispose();
  }
}

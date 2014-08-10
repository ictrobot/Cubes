package ethanjones.modularworld.side.common;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import ethanjones.modularworld.block.BlockManager;
import ethanjones.modularworld.block.factory.BlockFactories;
import ethanjones.modularworld.core.Branding;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.compatibility.Compatibility;
import ethanjones.modularworld.core.data.core.DataByte;
import ethanjones.modularworld.core.data.core.DataGroup;
import ethanjones.modularworld.core.data.core.DataInteger;
import ethanjones.modularworld.core.data.notation.DataNotation;
import ethanjones.modularworld.core.events.EventBus;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.core.settings.SettingsManager;
import ethanjones.modularworld.core.thread.Threads;
import ethanjones.modularworld.core.timing.Timing;
import ethanjones.modularworld.graphics.asset.AssetManager;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.debug.Debug;
import ethanjones.modularworld.world.World;

public abstract class ModularWorld implements ApplicationListener {

  public static Compatibility compatibility;
  public static AssetManager assetManager;
  public static FileHandle baseFolder;
  public static EventBus eventBus;
  public static SettingsManager settings;
  public static BlockManager blockManager;
  public static Timing timing;
  private static boolean setup;

  public static void setup() {
    Log.info(DataNotation.toString(new DataInteger(1234)));
    Log.info(DataNotation.toString(new DataByte((byte) 1)));

    DataGroup d = new DataGroup();
    d.setInteger("key", 1);
    DataGroup d1 = new DataGroup();
    d1.setInteger("ra", 12345);
    DataGroup d11 = new DataGroup();
    d11.setDouble("nor", 1.25);
    d11.setDouble("inv", 0.75);
    DataGroup d12 = new DataGroup();
    d12.setInteger("value", 10);
    d12.setBoolean("setting", true);
    d1.setGroup("Num", d11);
    d1.setGroup("Setting", d12);
    d.setGroup("Stuff", d1);
    Log.info(DataNotation.toString(d));
    if (setup) return;
    Log.info(Branding.NAME, Branding.DEBUG);
    if (compatibility == null)
      Log.error(new ModularWorldException("No Compatibility module for this platform: " + Gdx.app.getType().name() + ", OS: " + System.getProperty("os.name") + ", Arch:" + System.getProperty("os.arch")));
    compatibility.logEnvironment();
    Debug.printProperties();

    eventBus = new EventBus();
    compatibility.init();

    baseFolder = compatibility.getBaseFolder();
    baseFolder.mkdirs();

    settings = new SettingsManager();
    Settings.processAll();
    settings.readFromFile();
    settings.print();

    assetManager = new AssetManager();

    blockManager = new BlockManager();

    BlockFactories.init();

    timing = new Timing();
    setup = true;

    Threads.init();

    NetworkingManager.readPort();
  }

  private final Side side;
  public World world;

  public ModularWorld(Side side) {
    this.side = side;
  }

  @Override
  public void create() {
    //TODO: Rewrite settings, have two classes "Client" and "Server"
    eventBus.register(this);
  }

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void render() {
    timing.update();
    NetworkingManager.getNetworking(side).processPackets();
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
    Threads.disposeExecutor();
    world.dispose();
  }
}

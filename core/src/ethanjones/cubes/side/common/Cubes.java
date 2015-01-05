package ethanjones.cubes.side.common;

import com.badlogic.gdx.Gdx;

import ethanjones.cubes.block.BlockManager;
import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.event.InitializationEvent;
import ethanjones.cubes.core.mod.event.PostInitializationEvent;
import ethanjones.cubes.core.mod.event.PreInitializationEvent;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.AdapterInterface;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.timing.TimeHandler;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.SimpleApplication;
import ethanjones.cubes.side.State;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.world.World;

public abstract class Cubes implements SimpleApplication, TimeHandler {

  public static final int tickMS = 25;
  private static boolean setup;
  private static AdapterInterface adapterInterface;

  public static void setup(AdapterInterface adapterInterface) {
    if (setup) return;
    if (Compatibility.get() == null) {
      Log.error(new CubesException("No Compatibility module for this platform: " + Gdx.app.getType().name() + ", OS: " + System.getProperty("os.name") + ", Arch:" + System.getProperty("os.arch")));
    }
    Cubes.adapterInterface = adapterInterface;

    Compatibility.get().getBaseFolder().mkdirs();

    Log.info(Branding.DEBUG); //Can't log till base folder setup
    Debug.printProperties();

    Compatibility.get().init(null);
    Compatibility.get().logEnvironment();

    BlockManager.preInit();
    Blocks.init();

    Assets.preInit();
    ModManager.init();
    ModManager.postModEvent(new PreInitializationEvent());

    Settings.init();
    ModManager.postModEvent(new InitializationEvent());

    Assets.init();
    if (!Adapter.isDedicatedServer()) Blocks.loadGraphics();
    Localization.load();
    Settings.print();
    ModManager.postModEvent(new PostInitializationEvent());
    BlockManager.postInit();

    setup = true;
  }

  public static CubesClient getClient() {
    return adapterInterface.getClient();
  }

  public static CubesServer getServer() {
    return adapterInterface.getServer();
  }

  private final Side side;
  public World world;
  public Thread thread;
  protected State state = new State();

  public Cubes(Side side) {
    this.side = side;
  }

  @Override
  public void create() {
    thread = Thread.currentThread();
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

  protected boolean shouldReturn() {
    if (state.canRun()) {
      return false;
    } else {
      if (state.isStopping()) {
        stop();
      } else if (!state.isSetup()) {
        Log.error("Cubes" + side.name() + " is not setup");
      }
      return true;
    }
  }

  @Override
  public void dispose() {
    if (!state.canDispose()) return;
    state.stopping();
    if (state.isSetup() && Thread.currentThread() == thread) {
      stop();
    }
  }

  protected void stop() {
    if (Thread.currentThread() != thread) return;
    synchronized (this) {
      write();
      NetworkingManager.getNetworking(side).stop();
      world.dispose();
      Sided.reset(side);
      state.stopped();
    }
    if (!Adapter.isDedicatedServer()) {
      switch (side) {
        case Client:
          Adapter.setClient(null);
          break;
        case Server:
          Adapter.setServer(null);
          break;
      }
    }
  }

  public void write() {
    Settings.write();
  }

  public void time(int interval) {
    if (interval == tickMS) tick();
  }

  protected void tick() {
    NetworkingManager.getNetworking(side).update();
  }

  public Thread getThread() {
    return thread;
  }
}

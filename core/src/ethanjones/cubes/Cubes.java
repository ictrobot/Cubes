package ethanjones.cubes;

import com.badlogic.gdx.Gdx;

import ethanjones.cubes.common.block.BlockManager;
import ethanjones.cubes.common.block.Blocks;
import ethanjones.cubes.common.core.localization.Localization;
import ethanjones.cubes.common.core.logging.Log;
import ethanjones.cubes.common.core.mod.ModManager;
import ethanjones.cubes.common.core.mod.event.InitializationEvent;
import ethanjones.cubes.common.core.mod.event.PostInitializationEvent;
import ethanjones.cubes.common.core.mod.event.PreInitializationEvent;
import ethanjones.cubes.platform.Adapter;
import ethanjones.cubes.platform.AdapterInterface;
import ethanjones.cubes.platform.Compatibility;
import ethanjones.cubes.common.core.settings.Settings;
import ethanjones.cubes.common.core.system.Branding;
import ethanjones.cubes.common.core.system.CubesException;
import ethanjones.cubes.common.core.system.Debug;
import ethanjones.cubes.common.core.timing.TimeHandler;
import ethanjones.cubes.client.graphics.Graphics;
import ethanjones.cubes.client.graphics.assets.Assets;
import ethanjones.cubes.common.networking.NetworkingManager;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.common.Sided;
import ethanjones.cubes.common.SimpleApplication;
import ethanjones.cubes.common.State;
import ethanjones.cubes.client.CubesClient;
import ethanjones.cubes.server.CubesServer;
import ethanjones.cubes.common.world.World;

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

    if (!Adapter.isDedicatedServer()) Graphics.init();

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

  @Override
  public void dispose() {
    if (!state.canDispose()) return;
    state.stopping();
    if (state.isSetup() && Thread.currentThread() == thread) {
      stop();
    }
  }

  protected boolean shouldReturn() {
    if (state.isRunning()) {
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

  public boolean isRunning() {
    return state.isRunning();
  }
}

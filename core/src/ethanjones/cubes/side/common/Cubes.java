package ethanjones.cubes.side.common;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.id.IDManager;
import ethanjones.cubes.core.json.JsonLoader;
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
import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.core.util.PerSecond;
import ethanjones.cubes.entity.EntityManager;
import ethanjones.cubes.graphics.Graphics;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.server.CubesServer;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.light.WorldLightHandler;

import com.badlogic.gdx.Gdx;

import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class Cubes {

  public static final int tickMS = 25;
  private static boolean preInit, init;
  private static AdapterInterface adapterInterface;

  private static ConcurrentLinkedQueue<Runnable> updateRunnables = new ConcurrentLinkedQueue<>();
  private static ConcurrentLinkedQueue<Runnable> tickRunnables = new ConcurrentLinkedQueue<>();

  public static void preInit(AdapterInterface adapterInterface) {
    if (preInit) return;
    preInit = true;
    if (Compatibility.get() == null) {
      Log.error(new CubesException("No Compatibility module for this platform: " + Gdx.app.getType().name() + ", OS: " + System.getProperty("os.name") + ", Arch:" + System.getProperty("os.arch")));
    }
    Cubes.adapterInterface = adapterInterface;

    Compatibility.get().makeBaseFolder();

    Log.info(Branding.DEBUG); //Can't log till base folder setup
    Log.info("https://github.com/ictrobot/Cubes");
    Debug.printProperties();

    Compatibility.get().logEnvironment();
    Executor.init();

    Assets.preInit();

  }
  
  public static void init() {
    if (!preInit) throw new CubesException("Cubes.init called before Cubes.preInit");
    if (init) return;
    init = true;
  
    JsonLoader.loadCore();
    Blocks.init();
    ModManager.init();
    JsonLoader.firstStage();
  
    Compatibility.get().preInit();
    ModManager.postModEvent(new PreInitializationEvent());
  
    JsonLoader.secondStage();
    Settings.init();
    Compatibility.get().init();
    ModManager.postModEvent(new InitializationEvent());
  
    Assets.init();
    Localization.load();
    Settings.print();
    Compatibility.get().postInit();
    ModManager.postModEvent(new PostInitializationEvent());
    IDManager.loaded();
    EntityManager.loaded();
  
    if (!Adapter.isDedicatedServer()) Graphics.init();
  }

  public static CubesClient getClient() {
    return adapterInterface.getClient();
  }

  public static CubesServer getServer() {
    return adapterInterface.getServer();
  }

  public static boolean cubesSetup() {
    return init;
  }

  private final Side side;
  public World world;
  public Thread thread;
  protected State state = new State();
  public final PerSecond ticksPerSecond = new PerSecond(10);

  public Cubes(Side side) {
    this.side = side;
  }

  public void create() {
    thread = Thread.currentThread();
    Side.setup(side);
    Compatibility.get().sideInit(side);
    Side.getSidedEventBus().register(this);
    Side.getSidedEventBus().register(new WorldLightHandler());
  }

  // call as often as possible
  protected void update() {
    Runnable runnable;
    while ((runnable = updateRunnables.poll()) != null) {
      runnable.run();
    }

    NetworkingManager.getNetworking(side).processPackets();
    Side.getTiming().update();
  }
  
  // call once every tickMS
  protected void tick() {
    Runnable runnable;
    while ((runnable = tickRunnables.poll()) != null) {
      runnable.run();
    }

    ticksPerSecond.tick();
    world.tick();
    NetworkingManager.getNetworking(side).update();
  }
  
  public void write() {
    world.save();
    Settings.write();
  }

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
      Side.reset(side);
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

  public Thread getThread() {
    return thread;
  }

  public boolean isRunning() {
    return state.isRunning();
  }

  /** Allows injecting into the main thread. The runnable will be run ASAP at the start of the next update */
  public void queueForNextUpdate(Runnable runnable) {
    updateRunnables.add(runnable);
  }

  /** Allows injecting into the main thread. The runnable will be run at the start of the next tick */
  public void queueForNextTick(Runnable runnable) {
    tickRunnables.add(runnable);
  }
}

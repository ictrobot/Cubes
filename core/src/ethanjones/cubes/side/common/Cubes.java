package ethanjones.cubes.side.common;

import com.badlogic.gdx.Gdx;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.messaging.Message;
import ethanjones.cubes.core.messaging.MessageManager;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.event.InitializationEvent;
import ethanjones.cubes.core.mod.event.PostInitializationEvent;
import ethanjones.cubes.core.mod.event.PreInitializationEvent;
import ethanjones.cubes.core.platform.AdapterInterface;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.platform.GraphicalAdapter;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.core.system.Branding;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;
import ethanjones.cubes.core.system.Threads;
import ethanjones.cubes.core.timing.TimeHandler;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.menus.WaitingMenu;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.side.ControlMessage;
import ethanjones.cubes.side.ControlMessage.Status;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.SimpleApplication;
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

    Blocks.init();

    Assets.preInit();
    ModManager.init();
    ModManager.postModEvent(new PreInitializationEvent());

    Settings.init();
    Threads.init();
    ModManager.postModEvent(new InitializationEvent());

    Assets.init();
    Localization.load();
    Settings.print();
    ModManager.postModEvent(new PostInitializationEvent());

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
  protected boolean stopped;

  public Cubes(Side side) {
    this.side = side;
  }

  @Override
  public void create() {
    thread = Thread.currentThread();
    stopped = false;
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
  public final void dispose() {
    ControlMessage controlMessage = new ControlMessage();
    controlMessage.status = Status.Stop;
    MessageManager.sendMessage(controlMessage, this);
  }

  protected boolean checkStop() {
    if (!MessageManager.hasMessages(this)) return false;
    for (Message message : MessageManager.getMessages(this)) {
      if (message instanceof ControlMessage) {
        if (((ControlMessage) message).status == Status.Stop) {
          stop();
          if (message.from == null) return true;
          ControlMessage controlMessage = new ControlMessage();
          controlMessage.status = Status.Stopped;
          controlMessage.from = this;
          MessageManager.sendMessage(controlMessage, message.from);
          return true;
        }
      }
    }
    return false;
  }

  protected void stop() {
    write();
    stopped = true;
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

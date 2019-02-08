package ethanjones.cubes.side.server;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.entity.living.player.PlayerPlaceBlockEvent;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.event.StartingServerEvent;
import ethanjones.cubes.core.mod.event.StoppingServerEvent;
import ethanjones.cubes.core.performance.Performance;
import ethanjones.cubes.core.performance.PerformanceTags;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.timing.TimeHandler;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.side.server.command.CommandManager;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.server.WorldServer;

import com.badlogic.gdx.math.WindowedMean;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public abstract class CubesServer extends Cubes implements TimeHandler {

  private static final int SAVE_TIME = 60000;
  private static final AtomicLong lastUpdateTime = new AtomicLong();
  private final Save save;
  public final WindowedMean meanUpdateMS = new WindowedMean(50);

  public CubesServer(Save save) {
    super(Side.Server);
    this.save = save;
  }

  @Override
  public void create() {
    if (state.isSetup()) return;
    super.create();
    save.readIDManager();
    CommandManager.reset();
    NetworkingManager.serverInit();

    world = new WorldServer(save);

    Side.getTiming().addHandler(this, SAVE_TIME);

    ModManager.postModEvent(new StartingServerEvent());

    lastUpdateTime.set(System.currentTimeMillis());
    state.setup();
  }
  
  public void loop() {
    long nextTickTime = System.currentTimeMillis() + tickMS;
    int behindTicks = 0;
    while (state.isRunning()) {
      long diff = nextTickTime - System.currentTimeMillis();
      if (diff < 0) {
        behindTicks += 1 + (-diff / tickMS);
      }
      if (behindTicks == 0) {
        while (diff > 1) {
          if (diff > 3) update();
          try {
            Thread.sleep(1);
          } catch (InterruptedException e) {
            Log.error(e);
            break;
          }
          diff = nextTickTime - System.currentTimeMillis();
        }
      } else if (behindTicks >= (1000 / tickMS)) {
        Log.warning("Skipping " + behindTicks + " ticks");
        nextTickTime += behindTicks * tickMS;
        behindTicks = 0;
      } else {
        behindTicks--;
      }
      update();

      long tickStartNS = System.nanoTime();
      tick();
      long tickEndNS = System.nanoTime();
      meanUpdateMS.addValue((float) (tickEndNS - tickStartNS) / 1000000f);

      nextTickTime += tickMS;
    }
  }
  
  @Override
  protected void tick() {
    Performance.start(PerformanceTags.SERVER_TICK);
    super.tick();
    Performance.start(PerformanceTags.SERVER_PLAYERS_UPDATE);
    for (ClientIdentifier clientIdentifier : getAllClients()) {
      clientIdentifier.getPlayerManager().update();
    }
    Performance.stop(PerformanceTags.SERVER_PLAYERS_UPDATE);
    Performance.stop(PerformanceTags.SERVER_TICK);
  }
  
  @Override
  protected void update() {
    lastUpdateTime.set(System.currentTimeMillis());
    super.update();
    Compatibility.get().update();
  }
  
  @Override
  protected void stop() {
    if (state.hasStopped() || !state.isSetup()) return;
    ModManager.postModEvent(new StoppingServerEvent());
    super.stop();
    if (isDedicated()) Adapter.quit();
  }

  @Override
  public void time(int interval) {
    if (shouldReturn()) return;
    if (interval == SAVE_TIME) world.save();
  }

  @EventHandler
  public void placeMeta(PlayerPlaceBlockEvent event) {
    if (event.getBlock() == Blocks.log) {
      switch (event.getBlockIntersection().getBlockFace()) {
        case posY:
        case negY:
          event.setMeta(0);
          break;
        case posX:
        case negX:
          event.setMeta(1);
          break;
        case posZ:
        case negZ:
          event.setMeta(2);
          break;
      }
    }
  }

  public abstract boolean isDedicated();

  public abstract List<ClientIdentifier> getAllClients();

  public abstract ClientIdentifier getClient(SocketMonitor socketMonitor);

  public abstract ClientIdentifier getClient(String username);

  public abstract void addClient(ClientIdentifier clientIdentifier);

  public abstract void removeClient(SocketMonitor socketMonitor);
  
  public static long lastUpdateTime() {
    return lastUpdateTime.get();
  }
}

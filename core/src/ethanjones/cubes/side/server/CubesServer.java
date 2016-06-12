package ethanjones.cubes.side.server;

import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.event.StartingServerEvent;
import ethanjones.cubes.core.mod.event.StoppingServerEvent;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.timing.TimeHandler;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.command.CommandManager;
import ethanjones.cubes.world.generator.smooth.SmoothWorld;
import ethanjones.cubes.world.server.WorldServer;
import ethanjones.cubes.world.save.Save;

import java.util.List;

public abstract class CubesServer extends Cubes implements TimeHandler {

  private final Save save;

  public CubesServer() {
    this(null);
  }

  public CubesServer(Save save) {
    super(Side.Server);
    this.save = save;
  }

  @Override
  public void create() {
    if (state.isSetup()) return;
    super.create();
    CommandManager.reset();
    NetworkingManager.serverInit();

    world = new WorldServer(new SmoothWorld(), save);

    //Sided.getTiming().addHandler(this, 250);

    ModManager.postModEvent(new StartingServerEvent());

    state.setup();
  }

  @Override
  public void render() {
    if (shouldReturn()) return;
    super.render();
    for (ClientIdentifier clientIdentifier : getAllClients()) {
      clientIdentifier.getPlayerManager().update();
    }
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
    super.time(interval);
    //if (interval != 250) return;
    //world.setBlock(Blocks.dirt, (int) (Math.random() * 16), (int) (8 + (Math.random() * 7)), (int) (Math.random() * 16));
  }

  public abstract boolean isDedicated();

  public abstract List<ClientIdentifier> getAllClients();

  public abstract ClientIdentifier getClient(SocketMonitor socketMonitor);

  public abstract ClientIdentifier getClient(String username);

  public abstract void addClient(ClientIdentifier clientIdentifier);

  public abstract void removeClient(SocketMonitor socketMonitor);
}

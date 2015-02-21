package ethanjones.cubes.server;

import java.util.List;

import ethanjones.cubes.common.block.Blocks;
import ethanjones.cubes.common.core.mod.ModManager;
import ethanjones.cubes.common.core.mod.event.StartingServerEvent;
import ethanjones.cubes.common.core.mod.event.StoppingServerEvent;
import ethanjones.cubes.platform.Adapter;
import ethanjones.cubes.common.core.timing.TimeHandler;
import ethanjones.cubes.common.networking.NetworkingManager;
import ethanjones.cubes.common.networking.server.ClientIdentifier;
import ethanjones.cubes.common.networking.socket.SocketMonitor;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.common.Sided;
import ethanjones.cubes.Cubes;
import ethanjones.cubes.server.command.CommandManager;
import ethanjones.cubes.common.world.generator.BasicTerrainGenerator;
import ethanjones.cubes.common.world.server.WorldServer;

public abstract class CubesServer extends Cubes implements TimeHandler {

  public CubesServer() {
    super(Side.Server);
  }

  @Override
  public void create() {
    if (state.isSetup()) return;
    super.create();
    CommandManager.reset();
    NetworkingManager.serverInit();

    world = new WorldServer(new BasicTerrainGenerator());

    Sided.getTiming().addHandler(this, 250);

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
    if (interval != 250) return;
    world.setBlock(Blocks.dirt, (int) (Math.random() * 16), (int) (8 + (Math.random() * 7)), (int) (Math.random() * 16));
  }

  public abstract boolean isDedicated();

  public abstract List<ClientIdentifier> getAllClients();

  public abstract ClientIdentifier getClient(SocketMonitor socketMonitor);

  public abstract ClientIdentifier getClient(String username);

  public abstract void addClient(ClientIdentifier clientIdentifier);

  public abstract void removeClient(SocketMonitor socketMonitor);
}

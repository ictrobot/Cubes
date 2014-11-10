package ethanjones.cubes.side.server;

import java.util.HashMap;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.event.StartingServerEvent;
import ethanjones.cubes.core.mod.event.StoppingServerEvent;
import ethanjones.cubes.core.timing.TimeHandler;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.server.ServerNetworkingParameter;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.WorldServer;
import ethanjones.cubes.world.generator.BasicWorldGenerator;

public class CubesServer extends Cubes implements TimeHandler {

  public static CubesServer instance;
  private final ServerNetworkingParameter serverNetworkingParameter;
  public CubesServerThread thread; //only on singleplayer
  public HashMap<SocketMonitor, PlayerManager> playerManagers;
  private boolean disposed = false;

  public CubesServer(ServerNetworkingParameter serverNetworkingParameter) {
    super(Side.Server);
    this.serverNetworkingParameter = serverNetworkingParameter;
    playerManagers = new HashMap<SocketMonitor, PlayerManager>();
  }

  @Override
  public void create() {
    super.create();
    NetworkingManager.startServer(serverNetworkingParameter);
    if (CubesClient.instance != null && CubesClient.instance.wait != null) {
      synchronized (CubesClient.instance.wait) {
        CubesClient.instance.wait.notifyAll();
      }
    }

    world = new WorldServer(new BasicWorldGenerator());

    Sided.getTiming().addHandler(this, 250);

    ModManager.postModEvent(new StartingServerEvent());
  }

  @Override
  public void dispose() {
    if (disposed) return;
    ModManager.postModEvent(new StoppingServerEvent());
    super.dispose();
    disposed = true;
  }

  @Override
  public void render() {
    super.render();

    for (PlayerManager playerManager : playerManagers.values()) {
      playerManager.update();
    }
  }

  @Override
  public void time(int interval) {
    super.time(interval);
    if (interval != 250) return;
    world.setBlock(Blocks.dirt, (int) (Math.random() * 16), (int) (8 + (Math.random() * 7)), (int) (Math.random() * 16));
  }
}

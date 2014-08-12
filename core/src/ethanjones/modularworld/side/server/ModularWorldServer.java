package ethanjones.modularworld.side.server;

import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.block.factory.BlockFactories;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.timing.TimeHandler;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.server.ServerNetworkingParameter;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.world.WorldServer;
import ethanjones.modularworld.world.generator.BasicWorldGenerator;

public class ModularWorldServer extends ModularWorld implements TimeHandler {

  public static ModularWorldServer instance;
  private final ServerNetworkingParameter serverNetworkingParameter;
  public Array<PlayerManager> playerManagers;

  public ModularWorldServer(ServerNetworkingParameter serverNetworkingParameter) {
    super(Side.Server);
    this.serverNetworkingParameter = serverNetworkingParameter;
    ModularWorldServer.instance = this;
    playerManagers = new Array<PlayerManager>();
  }

  @Override
  public void create() {
    super.create();
    NetworkingManager.startServer(serverNetworkingParameter);

    world = new WorldServer(new BasicWorldGenerator());

    timing.addHandler(this, 1000);
  }

  @Override
  public void time(int interval) {
    world.setBlockFactory(BlockFactories.dirt, (int) (Math.random() * 16), (int) (8 + (Math.random() * 7)), (int) (Math.random() * 16));
    for (Thread thread : Thread.getAllStackTraces().keySet()) {
      Log.info(thread.getName() + " " + thread.getThreadGroup().getName());
    }
  }
}

package ethanjones.modularworld.side.server;

import com.badlogic.gdx.utils.Array;
import ethanjones.modularworld.block.factory.BlockFactories;
import ethanjones.modularworld.core.timing.TimeHandler;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.asset.AssetManager;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.world.WorldServer;
import ethanjones.modularworld.world.generator.BasicWorldGenerator;

public class ModularWorldServer extends ModularWorld implements TimeHandler {

  public static ModularWorldServer instance;
  public Array<PlayerManager> playerManagers;

  public ModularWorldServer() {
    super(Side.Server);
    ModularWorldServer.instance = this;
    playerManagers = new Array<PlayerManager>();
  }

  @Override
  public void create() {
    super.create();
    NetworkingManager.startServer();

    if (compatibility.graphics()) {
      assetManager = new AssetManager();
      compatibility.getAssets(assetManager);
      GraphicsHelper.init(assetManager);
    }

    world = new WorldServer(new BasicWorldGenerator());

    timing.addHandler(this, 100);
  }

  @Override
  public void time(int interval) {
    world.setBlockFactory(BlockFactories.dirt, (int) (Math.random() * 33), (int) (8 + (Math.random() * 25)), (int) (Math.random() * 33));
  }
}

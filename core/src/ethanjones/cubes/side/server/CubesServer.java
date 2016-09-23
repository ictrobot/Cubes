package ethanjones.cubes.side.server;

import ethanjones.cubes.block.Blocks;
import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.entity.living.player.PlayerBreakBlockEvent;
import ethanjones.cubes.core.event.entity.living.player.PlayerPlaceBlockEvent;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.event.StartingServerEvent;
import ethanjones.cubes.core.mod.event.StoppingServerEvent;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.timing.TimeHandler;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.core.util.VectorUtil;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.server.ClientIdentifier;
import ethanjones.cubes.networking.socket.SocketMonitor;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.server.command.CommandManager;
import ethanjones.cubes.world.save.Save;
import ethanjones.cubes.world.server.WorldServer;

import com.badlogic.gdx.math.Vector3;

import java.util.List;

public abstract class CubesServer extends Cubes implements TimeHandler {

  private static final int SAVE_TIME = 60000;
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

    world = new WorldServer(save);

    Sided.getTiming().addHandler(this, SAVE_TIME);
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
    if (interval == SAVE_TIME) world.save();
    //if (interval != 250) return;
    //world.setBlock(Blocks.dirt, (int) (Math.random() * 16), (int) (8 + (Math.random() * 7)), (int) (Math.random() * 16));
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
    } else if (event.getBlock() == Blocks.chest) {
      Vector3 pos = event.getPlayer().position.cpy();
      pos.sub(event.getBlockReference().asVector3());
      pos.nor();
      BlockFace blockFace = VectorUtil.directionXZ(pos);
      if (blockFace == null || blockFace == BlockFace.posX) {
        event.setMeta(0);
      } else if (blockFace == BlockFace.negX) {
        event.setMeta(1);
      } else if (blockFace == BlockFace.posZ) {
        event.setMeta(2);
      } else if (blockFace == BlockFace.negZ) {
        event.setMeta(3);
      }
    }
  }

  @EventHandler
  public void breakMeta(PlayerBreakBlockEvent event) {
    if (!(event.getBlock() == Blocks.log || event.getBlock() == Blocks.chest)) return;
    event.setMeta(0);
  }

  public abstract boolean isDedicated();

  public abstract List<ClientIdentifier> getAllClients();

  public abstract ClientIdentifier getClient(SocketMonitor socketMonitor);

  public abstract ClientIdentifier getClient(String username);

  public abstract void addClient(ClientIdentifier clientIdentifier);

  public abstract void removeClient(SocketMonitor socketMonitor);
}

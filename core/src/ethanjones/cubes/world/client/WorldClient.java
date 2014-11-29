package ethanjones.cubes.world.client;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.AreaReference;

public class WorldClient extends World {

  Future maintenanceFuture;

  public WorldClient() {
    super(null);
    maintenanceFuture = Executor.scheduleAtFixedRate(new WorldClientMaintenance(this), 10, 10, TimeUnit.SECONDS);
  }

  @Override
  protected void requestAreaInternal(AreaReference areaReference) {

  }

  @Override
  public void dispose() {
    super.dispose();
    maintenanceFuture.cancel(false);
  }
}

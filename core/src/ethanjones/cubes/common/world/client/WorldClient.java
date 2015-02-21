package ethanjones.cubes.common.world.client;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ethanjones.cubes.common.util.Executor;
import ethanjones.cubes.common.world.World;
import ethanjones.cubes.common.world.reference.AreaReference;

public class WorldClient extends World {

  Future maintenanceFuture;

  public WorldClient() {
    super(null);
    maintenanceFuture = Executor.scheduleAtFixedRate(new WorldClientMaintenance(this), 10, 10, TimeUnit.SECONDS);
  }

  @Override
  public void requestArea(AreaReference areaReference) {

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

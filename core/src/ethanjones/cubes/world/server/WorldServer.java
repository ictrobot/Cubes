package ethanjones.cubes.world.server;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.thread.ThreadedWorld;

public class WorldServer extends World {

  Future maintenanceFuture;

  public WorldServer(TerrainGenerator terrainGenerator) {
    super(terrainGenerator);
    maintenanceFuture = Executor.scheduleAtFixedRate(new WorldServerMaintenance(this), 10, 10, TimeUnit.SECONDS);
  }

  public void requestAreaInternal(AreaReference areaReference) {
    Executor.execute(new ThreadedWorld.GenerateCallable(areaReference, this));
  }

  @Override
  public void dispose() {
    super.dispose();
    maintenanceFuture.cancel(false);
  }

}

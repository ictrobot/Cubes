package ethanjones.cubes.common.world.server;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import ethanjones.cubes.common.util.Executor;
import ethanjones.cubes.common.world.World;
import ethanjones.cubes.common.world.generator.TerrainGenerator;
import ethanjones.cubes.common.world.reference.AreaReference;
import ethanjones.cubes.common.world.thread.GenerateWorldCallable;

public class WorldServer extends World {

  Future maintenanceFuture;

  public WorldServer(TerrainGenerator terrainGenerator) {
    super(terrainGenerator);
    maintenanceFuture = Executor.scheduleAtFixedRate(new WorldServerMaintenance(this), 10, 10, TimeUnit.SECONDS);
  }

  public void requestAreaInternal(AreaReference areaReference) {
    Executor.execute(new GenerateWorldCallable(areaReference, this));
  }

  @Override
  public void dispose() {
    super.dispose();
    maintenanceFuture.cancel(false);
  }

}

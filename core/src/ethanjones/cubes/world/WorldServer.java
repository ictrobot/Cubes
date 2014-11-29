package ethanjones.cubes.world;

import ethanjones.cubes.core.system.Executor;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.thread.GenerateWorldCallable;

public class WorldServer extends World {

  public WorldServer(TerrainGenerator terrainGenerator) {
    super(terrainGenerator);
  }

  public void requestAreaInternal(AreaReference areaReference) {
    Executor.execute(new GenerateWorldCallable(areaReference, this));
  }

}

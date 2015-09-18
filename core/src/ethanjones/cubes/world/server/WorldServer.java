package ethanjones.cubes.world.server;

import ethanjones.cubes.world.World;
import ethanjones.cubes.world.generator.TerrainGenerator;
import ethanjones.cubes.world.reference.multi.MultiAreaReference;
import ethanjones.cubes.world.thread.WorldTasks;

public class WorldServer extends World {

  public WorldServer(TerrainGenerator terrainGenerator) {
    super(terrainGenerator);
  }

  @Override
  public void requestRegion(MultiAreaReference references) {
    WorldTasks.request(this, references);
  }

  @Override
  public void dispose() {
    super.dispose();
  }

}

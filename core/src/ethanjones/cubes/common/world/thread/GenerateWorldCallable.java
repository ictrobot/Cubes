package ethanjones.cubes.common.world.thread;

import java.util.concurrent.Callable;

import ethanjones.cubes.common.core.event.world.generation.GenerationEvent;
import ethanjones.cubes.common.world.reference.AreaReference;
import ethanjones.cubes.common.world.server.WorldServer;
import ethanjones.cubes.common.world.storage.Area;

public class GenerateWorldCallable implements Callable<Area> {

  private final AreaReference areaReference;
  private final WorldServer world;

  public GenerateWorldCallable(AreaReference areaReference, WorldServer world) {
    this.areaReference = areaReference;
    this.world = world;
  }

  @Override
  public Area call() throws Exception {
    Area area = new Area(areaReference.areaX, areaReference.areaZ);
    world.getTerrainGenerator().generate(area);
    new GenerationEvent(area, areaReference).post();
    area.updateAll();
    world.setAreaInternal(areaReference, area);
    return area;
  }
}

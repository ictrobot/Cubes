package ethanjones.cubes.world.thread;

import java.util.concurrent.Callable;

import ethanjones.cubes.core.event.world.generation.GenerationEvent;
import ethanjones.cubes.world.WorldServer;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

public class GenerateWorldCallable implements Callable<Area> {

  private final AreaReference areaReference;
  private final WorldServer world;

  public GenerateWorldCallable(AreaReference areaReference, WorldServer world) {
    this.areaReference = areaReference;
    this.world = world;
  }

  @Override
  public Area call() throws Exception {
    Area area = new Area(areaReference.areaX, areaReference.areaY, areaReference.areaZ, false);
    world.getWorldGenerator().generate(area);
    new GenerationEvent(area, areaReference).post();
    area.generated = true;
    world.setAreaInternal(areaReference, area);
    return area;
  }
}

package ethanjones.modularworld.world.thread;

import ethanjones.modularworld.core.events.world.generation.GenerationEvent;
import ethanjones.modularworld.world.WorldServer;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

import java.util.concurrent.Callable;

public class GenerateWorld implements Callable<Area> {

  private final AreaReference areaReference;
  private final WorldServer world;

  public GenerateWorld(AreaReference areaReference, WorldServer world) {
    this.areaReference = areaReference;
    this.world = world;
  }

  @Override
  public Area call() throws Exception {
    Area area = new Area(areaReference.areaX, areaReference.areaY, areaReference.areaZ, false);
    world.getWorldGenerator().generate(area);
    new GenerationEvent(area, areaReference.getAreaCoordinates()).post();
    area.generated = true;
    world.setAreaInternal(areaReference, area);
    return area;
  }
}

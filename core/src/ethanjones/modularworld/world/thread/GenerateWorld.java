package ethanjones.modularworld.world.thread;

import ethanjones.modularworld.core.events.world.generation.GenerationEvent;
import ethanjones.modularworld.world.WorldServer;
import ethanjones.modularworld.world.reference.AreaReference;
import ethanjones.modularworld.world.storage.Area;

import java.util.concurrent.Callable;

public class GenerateWorld implements Callable {

  private final AreaReference areaReference;
  private final WorldServer world;

  public GenerateWorld(AreaReference areaReference, WorldServer world) {
    this.areaReference = areaReference;
    this.world = world;
  }

  @Override
  public Object call() throws Exception {
    Area area = areaReference.newArea();
    world.getWorldGenerator().generate(area);
    new GenerationEvent(area, areaReference.getAreaCoordinates()).post();
    area.generated = true;
    world.setAreaInternal(areaReference, area);
    return area;
  }
}

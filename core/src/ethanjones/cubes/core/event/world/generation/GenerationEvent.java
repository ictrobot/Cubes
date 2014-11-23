package ethanjones.cubes.core.event.world.generation;

import ethanjones.cubes.core.event.world.WorldEvent;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

public class GenerationEvent extends WorldEvent {

  private final Area area;
  private final AreaReference areaReference;

  public GenerationEvent(Area area, AreaReference areaReference) {
    super(false);
    this.area = area;
    this.areaReference = areaReference;
  }

  public Area getArea() {
    return area;
  }

  public AreaReference getAreaReference() {
    return areaReference;
  }
}

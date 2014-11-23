package ethanjones.cubes.core.events.world.generation;

import ethanjones.cubes.core.events.Event;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

public class GenerationEvent extends Event {

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

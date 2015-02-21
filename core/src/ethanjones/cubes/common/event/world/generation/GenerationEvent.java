package ethanjones.cubes.common.event.world.generation;

import ethanjones.cubes.common.event.world.WorldEvent;
import ethanjones.cubes.common.world.reference.AreaReference;
import ethanjones.cubes.common.world.storage.Area;

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

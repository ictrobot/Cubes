package ethanjones.modularworld.core.events.world.generation;

import ethanjones.modularworld.core.events.Event;
import ethanjones.modularworld.world.coordinates.AreaCoordinates;
import ethanjones.modularworld.world.storage.Area;

public class GenerationEvent extends Event {

  private final Area area;
  private final AreaCoordinates areaCoordinates;

  public GenerationEvent(Area area, AreaCoordinates areaCoordinates) {
    super(false);
    this.area = area;
    this.areaCoordinates = areaCoordinates;
  }

  public Area getArea() {
    return area;
  }

  public AreaCoordinates getAreaCoordinates() {
    return areaCoordinates;
  }
}

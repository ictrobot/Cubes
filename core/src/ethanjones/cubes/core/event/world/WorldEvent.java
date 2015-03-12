package ethanjones.cubes.core.event.world;

import ethanjones.cubes.core.event.Event;

public class WorldEvent extends Event {

  public WorldEvent(boolean cancelable) {
    super(cancelable, false);
  }
}

package ethanjones.cubes.common.core.event.world;

import ethanjones.cubes.common.core.event.Event;

public class WorldEvent extends Event {

  public WorldEvent(boolean cancelable) {
    super(cancelable, false);
  }
}

package ethanjones.cubes.common.event.world;

import ethanjones.cubes.common.event.Event;

public class WorldEvent extends Event {

  public WorldEvent(boolean cancelable) {
    super(cancelable, false);
  }
}

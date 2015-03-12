package ethanjones.cubes.core.event.entity;

import ethanjones.cubes.core.event.Event;

public class EntityEvent extends Event {

  public EntityEvent(boolean cancelable) {
    super(cancelable, false);
  }
}

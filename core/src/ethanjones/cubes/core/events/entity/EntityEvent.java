package ethanjones.cubes.core.events.entity;

import ethanjones.cubes.core.events.Event;

public class EntityEvent extends Event {

  public EntityEvent(boolean cancelable) {
    super(cancelable);
  }
}

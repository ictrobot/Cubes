package ethanjones.cubes.common.event.entity;

import ethanjones.cubes.common.event.Event;

public class EntityEvent extends Event {

  public EntityEvent(boolean cancelable) {
    super(cancelable, false);
  }
}

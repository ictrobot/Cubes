package ethanjones.cubes.common.core.event.entity;

import ethanjones.cubes.common.core.event.Event;

public class EntityEvent extends Event {

  public EntityEvent(boolean cancelable) {
    super(cancelable, false);
  }
}

package ethanjones.modularworld.core.events.entity;

import ethanjones.modularworld.core.events.Event;

public class EntityEvent extends Event {

  public EntityEvent(boolean cancelable) {
    super(cancelable);
  }
}

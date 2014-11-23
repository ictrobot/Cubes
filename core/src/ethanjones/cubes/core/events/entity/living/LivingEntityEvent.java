package ethanjones.cubes.core.events.entity.living;

import ethanjones.cubes.core.events.entity.EntityEvent;

public class LivingEntityEvent extends EntityEvent {

  public LivingEntityEvent(boolean cancelable) {
    super(cancelable);
  }
}

package ethanjones.cubes.common.core.event.entity.living.player;

import ethanjones.cubes.common.core.event.entity.living.LivingEntityEvent;

public class PlayerEvent extends LivingEntityEvent {

  public PlayerEvent(boolean cancelable) {
    super(cancelable);
  }
}

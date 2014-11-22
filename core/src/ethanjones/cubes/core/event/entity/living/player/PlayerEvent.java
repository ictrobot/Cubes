package ethanjones.cubes.core.event.entity.living.player;

import ethanjones.cubes.core.event.entity.living.LivingEntityEvent;

public class PlayerEvent extends LivingEntityEvent {

  public PlayerEvent(boolean cancelable) {
    super(cancelable);
  }
}

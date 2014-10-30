package ethanjones.cubes.core.events.entity.living.player;

import ethanjones.cubes.core.events.entity.living.LivingEntityEvent;

public class PlayerEvent extends LivingEntityEvent {

  public PlayerEvent(boolean cancelable) {
    super(cancelable);
  }
}

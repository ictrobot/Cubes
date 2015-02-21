package ethanjones.cubes.common.event.entity.living.player;

import ethanjones.cubes.common.event.entity.living.LivingEntityEvent;

public class PlayerEvent extends LivingEntityEvent {

  public PlayerEvent(boolean cancelable) {
    super(cancelable);
  }
}

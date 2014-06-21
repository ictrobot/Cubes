package ethanjones.modularworld.core.events.entity.living.player;

import ethanjones.modularworld.core.events.entity.living.LivingEntityEvent;

public class PlayerEvent extends LivingEntityEvent {

  public PlayerEvent(boolean cancelable) {
    super(cancelable);
  }
}

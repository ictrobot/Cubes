package ethanjones.cubes.core.event.entity.living.player;

import ethanjones.cubes.core.event.entity.living.LivingEntityEvent;
import ethanjones.cubes.entity.living.player.Player;

public class PlayerEvent extends LivingEntityEvent {

  private final Player player;

  public PlayerEvent(Player player, boolean cancelable) {
    super(cancelable);
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }
}

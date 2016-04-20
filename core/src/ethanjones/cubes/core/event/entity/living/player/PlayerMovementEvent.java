package ethanjones.cubes.core.event.entity.living.player;

import ethanjones.cubes.entity.living.player.Player;

import com.badlogic.gdx.math.Vector3;

public class PlayerMovementEvent extends PlayerEvent {

  public final Vector3 newPosition;

  public PlayerMovementEvent(Player player, Vector3 newPosition) {
    super(player, true);
    this.newPosition = newPosition;
  }
}

package ethanjones.cubes.core.event.entity.living.player;

import com.badlogic.gdx.math.Vector3;
import ethanjones.cubes.entity.living.player.Player;

public class PlayerMovementEvent extends PlayerEvent {

  public final Vector3 oldPosition;
  public final Vector3 newPosition;

  public PlayerMovementEvent(Player player, Vector3 newPosition) {
    super(player, true);
    this.oldPosition = player.position.cpy();
    this.newPosition = newPosition;
  }
}

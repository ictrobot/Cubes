package ethanjones.cubes.core.event.entity.living.player;

import com.badlogic.gdx.math.Vector3;

public class PlayerMovementEvent extends PlayerEvent {

  public final Vector3 newPosition;

  public PlayerMovementEvent(Vector3 newPosition) {
    super(true);
    this.newPosition = newPosition;
  }
}

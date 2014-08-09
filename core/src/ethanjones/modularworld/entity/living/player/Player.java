package ethanjones.modularworld.entity.living.player;

import ethanjones.modularworld.entity.living.LivingEntity;
import ethanjones.modularworld.input.MovementHandler;

public class Player extends LivingEntity {

  private final String username;
  public MovementHandler movementHandler;

  public Player(String username) {
    super(20);
    this.username = username;
    this.movementHandler = new MovementHandler(this);
  }

}

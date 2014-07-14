package ethanjones.modularworld.entity.living.player;

import ethanjones.modularworld.entity.living.LivingEntity;
import ethanjones.modularworld.input.MovementHandler;

public class Player extends LivingEntity {

  public MovementHandler movementHandler;

  public Player() {
    super(20);
    this.movementHandler = new MovementHandler(this);
  }

}

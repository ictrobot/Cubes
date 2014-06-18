package ethanjones.modularworld.entity.living;

import ethanjones.modularworld.entity.Entity;

public class LivingEntity extends Entity {

  public int health;
  public int maxHealth;
  public int regenerationSpeed;

  public LivingEntity(int maxHealth) {
    super();
    this.gravity = true;
    this.health = maxHealth;
    this.maxHealth = maxHealth;
    this.regenerationSpeed = 0;
  }

  public void update() {
    super.update();
    //TODO: regeneration
  }
}

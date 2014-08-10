package ethanjones.modularworld.entity.living;

import ethanjones.modularworld.core.data.core.DataGroup;
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

  @Override
  public DataGroup write() {
    DataGroup data = super.write();
    data.setInteger("health", health);
    data.setInteger("maxHealth", maxHealth);
    data.setInteger("regenerationSpeed", regenerationSpeed);
    return data;
  }

  @Override
  public void read(DataGroup data) {
    super.read(data);
    health = data.getInteger("health");
    maxHealth = data.getInteger("maxHealth");
    regenerationSpeed = data.getInteger("regenerationSpeed");
  }
}

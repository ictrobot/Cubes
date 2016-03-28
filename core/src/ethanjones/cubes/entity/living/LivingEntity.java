package ethanjones.cubes.entity.living;

import ethanjones.cubes.entity.Entity;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.math.Vector3;

public class LivingEntity extends Entity {

  public int health;
  public int maxHealth;
  public int regenerationSpeed;

  public LivingEntity(String type, int maxHealth) {
    super(type);
    this.health = maxHealth;
    this.maxHealth = maxHealth;
    this.regenerationSpeed = 0;
  }

  public LivingEntity(String type, Vector3 position, Vector3 angle, int maxHealth) {
    super(type, position, angle);
    this.health = maxHealth;
    this.maxHealth = maxHealth;
    this.regenerationSpeed = 0;
  }

  public void update() {
    super.update();
    //TODO regeneration
  }

  @Override
  public DataGroup write() {
    DataGroup data = super.write();
    data.put("health", health);
    data.put("maxHealth", maxHealth);
    data.put("regenerationSpeed", regenerationSpeed);
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

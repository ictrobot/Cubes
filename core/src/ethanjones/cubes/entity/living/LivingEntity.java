package ethanjones.cubes.entity.living;

import com.badlogic.gdx.math.Vector3;
import ethanjones.cubes.entity.Entity;
import ethanjones.data.DataGroup;

public class LivingEntity extends Entity {

  public int health;
  public int maxHealth;

  public LivingEntity(String type, int maxHealth) {
    super(type);
    this.health = maxHealth;
    this.maxHealth = maxHealth;
  }

  public LivingEntity(String type, Vector3 position, Vector3 angle, int maxHealth) {
    super(type, position, angle);
    this.health = maxHealth;
    this.maxHealth = maxHealth;
  }

  public boolean update() {
    return super.update();
  }

  @Override
  public DataGroup write() {
    DataGroup data = super.write();
    data.put("health", health);
    data.put("maxHealth", maxHealth);
    return data;
  }

  @Override
  public void read(DataGroup data) {
    super.read(data);
    health = data.getInteger("health");
    maxHealth = data.getInteger("maxHealth");
  }
}

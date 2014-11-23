package ethanjones.cubes.entity.living.player;

import com.badlogic.gdx.graphics.Camera;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.entity.living.LivingEntity;

public class Player extends LivingEntity {

  public final String username;

  public Player(String username) {
    super(20);
    this.username = username;
  }

  public Player(Camera camera) {
    super(camera.position, camera.direction, 20);
    this.username = Settings.getStringSettingValue(Settings.USERNAME);
  }

}

package ethanjones.cubes.world.generator;

import com.badlogic.gdx.math.MathUtils;

public class RainStatus {

  public static final RainStatus NOT_RAINING = new RainStatus(false, 0);

  public final boolean raining;
  public final float rainRate;

  public RainStatus(boolean raining, float rainRate) {
    this.raining = raining;
    this.rainRate = MathUtils.clamp(rainRate, 0, 1);
  }

  @Override
  public String toString() {
    if (raining) {
      return "RAINING " + rainRate;
    }
    return "NOT RAINING";
  }
}

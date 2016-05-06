package ethanjones.cubes.core.util;

public class WorldGravity {

  public static float playerGravity(float time, float t) {
    return Math.min(f(8.825f, 7.825f, time, t), 0.2f);
  }

  public static float entityGravity(float time, float t) {
    return Math.min(f(5.5f, 4.75f, time, t), 0.2f);
  }

  public static float playerJump(float time, float t) {
    return f(-16f, 9f, time, t);
  }

  private static float f(float a, float b, float time, float t) {
    return t * (a * 2 * time + b) + (a * t * t);
  }
}

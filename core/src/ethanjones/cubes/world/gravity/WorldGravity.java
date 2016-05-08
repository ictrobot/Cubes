package ethanjones.cubes.world.gravity;

import ethanjones.cubes.entity.Entity;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;

import com.badlogic.gdx.math.Vector3;

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

  public static boolean doGravity(Vector3 result, World world, Entity entity, float t) {
    Vector3 pos = entity.position;
    float height = entity.height;
    boolean isPlayer = entity instanceof Player;
    result.set(pos);

    if (world.getArea(CoordinateConverter.area(pos.x), CoordinateConverter.area(pos.z)) == null) {
      entity.gravityTime = 0f;
      return false;
    }

    if (!onBlock(world, result, height)) {
      float g = isPlayer ? playerGravity(entity.gravityTime, t) : entityGravity(entity.gravityTime, t);

      entity.gravityTime += t;
      result.y -= g;
    }

    if (onBlock(world, result, height)) {
      entity.gravityTime = 0f;
      result.y = getBlockY(result, height) + 1 + height;
    }
    return !result.equals(entity.position);
  }

  private static int getBlockY(Vector3 pos, float height) {
    float f = pos.y - height;
    return CoordinateConverter.block(f - 0.01f);
  }

  private static boolean onBlock(World world, Vector3 pos, float height) {
    int y = getBlockY(pos, height);
    return world.getBlock(CoordinateConverter.block(pos.x), y, CoordinateConverter.block(pos.z)) != null;
  }

  private static float f(float a, float b, float time, float t) {
    return t * (a * 2 * time + b) + (a * t * t);
  }
}

package ethanjones.cubes.world.gravity;

import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;

import com.badlogic.gdx.math.Vector3;

public class WorldGravity {

  public static int getBlockY(Vector3 pos, float height) {
    float f = pos.y - height;
    return CoordinateConverter.block(f - 0.01f);
  }

  public static boolean onBlock(World w, Vector3 pos, float height, float r) {
    int y = getBlockY(pos, height);
    boolean b = isBlock(w, pos.x, y, pos.z);
    if (r == 0f) return b;
    return isBlock(w, pos.x + r, y, pos.z) || isBlock(w, pos.x - r, y, pos.z) || isBlock(w, pos.x, y, pos.z + r) || isBlock(w, pos.x, y, pos.z - r);
  }

  private static boolean isBlock(World world, float x, int y, float z) {
    return world.getBlock(CoordinateConverter.block(x), y, CoordinateConverter.block(z)) != null;
  }
}

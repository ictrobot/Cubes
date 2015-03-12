package ethanjones.cubes.graphics.world;

import com.badlogic.gdx.math.Vector3;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.BlockReference;

import static java.lang.Math.floor;
import static java.lang.Math.signum;

public class RayTracing {

  public static class BlockIntersection {

    private final BlockReference blockReference;
    private final BlockFace blockFace;

    public BlockIntersection(BlockReference blockReference, BlockFace blockFace) {
      this.blockReference = blockReference;
      this.blockFace = blockFace;
    }

    public BlockReference getBlockReference() {
      return blockReference;
    }

    public BlockFace getBlockFace() {
      return blockFace;
    }
  }

  public static BlockIntersection getBlockIntersection(Vector3 origin, Vector3 direction, World world) {
    return raycast(origin, direction, 6, world);
  }

  /**
   * https://github.com/kpreid/cubes/blob/c5e61fa22cb7f9ba03cd9f22e5327d738ec93969/world.js#L317
   */
  private static BlockIntersection raycast(Vector3 origin, Vector3 direction, int radius, World world) {
    if (direction.x == 0 && direction.y == 0 && direction.z == 0) return null;

    int x = (int) floor(origin.x);
    int y = (int) floor(origin.y);
    int z = (int) floor(origin.z);

    float dx = direction.x;
    float dy = direction.y;
    float dz = direction.z;

    float stepX = signum(dx);
    float stepY = signum(dy);
    float stepZ = signum(dz);

    float tMaxX = intbound(origin.x, direction.x);
    float tMaxY = intbound(origin.y, direction.y);
    float tMaxZ = intbound(origin.z, direction.z);

    float tDeltaX = stepX / direction.x;
    float tDeltaY = stepY / direction.y;
    float tDeltaZ = stepZ / direction.z;

    BlockFace face = BlockFace.posY; //current block face?

    //radius /= Math.sqrt(direction.x * direction.x + direction.y * direction.y + direction.z * direction.z);
    //no effect since direction is normalised

    while (true) {
      if (world.getBlock(x, y, z) != null) {
        return new BlockIntersection(new BlockReference().setFromBlockCoordinates(x, y, z), face);
      }

      if (tMaxX < tMaxY) {
        if (tMaxX < tMaxZ) {          // tMaxX < tMaxY && tMaxX < tMaxZ
          if (tMaxX > radius) break;
          x += stepX;
          tMaxX += tDeltaX;
          face = getFace(-stepX, 0, 0);
        } else {                      // tMaxX < tMaxY && tMaxX > tMaxZ
          if (tMaxZ > radius) break;
          z += stepZ;
          tMaxZ += tDeltaZ;
          face = getFace(0, 0, -stepZ);
        }
      } else {
        if (tMaxY < tMaxZ) {          // tMaxX > tMaxY && tMaxY < tMaxZ
          if (tMaxY > radius) break;
          y += stepY;
          tMaxY += tDeltaY;
          face = getFace(0, -stepY, 0);
        } else {                      // tMaxX > tMaxY && tMaxY > tMaxZ
          if (tMaxZ > radius) break;
          z += stepZ;
          tMaxZ += tDeltaZ;
          face = getFace(0, 0, -stepZ);
        }
      }
    }
    return null;
  }

  private static BlockFace getFace(float x, float y, float z) {
    if (x > 0 && y == 0 && z == 0) return BlockFace.posX;
    if (x < 0 && y == 0 && z == 0) return BlockFace.negX;

    if (x == 0 && y > 0 && z == 0) return BlockFace.posY;
    if (x == 0 && y < 0 && z == 0) return BlockFace.negY;

    if (x == 0 && y == 0 && z > 0) return BlockFace.posZ;
    if (x == 0 && y == 0 && z < 0) return BlockFace.negZ;

    return null;
  }

  private static float intbound(float s, float ds) {
    if (ds < 0) return intbound(-s, -ds);
    return (1 - (s % 1)) / ds;
  }
}

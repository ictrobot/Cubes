package ethanjones.cubes.world.collision;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.BlockReference;

import com.badlogic.gdx.math.Vector3;

public class BlockIntersection {

  private final BlockReference blockReference;
  private final BlockFace blockFace;
  private final Block block;
  private final int blockMeta;
  private final float rayLength;

  private BlockIntersection(BlockReference blockReference, BlockFace blockFace, Block block, int blockMeta, float rayLength) {
    this.blockReference = blockReference;
    this.blockFace = blockFace;
    this.block = block;
    this.blockMeta = blockMeta;
    this.rayLength = rayLength;
  }

  public BlockReference getBlockReference() {
    return blockReference;
  }

  public BlockFace getBlockFace() {
    return blockFace;
  }
  
  public Block getBlock() {
    return block;
  }
  
  public int getBlockMeta() {
    return blockMeta;
  }

  public float getRayLength() {
    return rayLength;
  }
  
  public static BlockIntersection getBlockIntersection(Vector3 origin, Vector3 direction, World world) {
    return intersection(origin, direction, 6, world);
  }

  public static BlockIntersection intersection(Vector3 origin, Vector3 direction, int radius, World world) {
    // A Fast Voxel Traversal Algorithm for Ray Tracing
    // http://www.cse.yorku.ca/~amana/research/grid.pdf

    BlockReference blockReference = new BlockReference().setFromVector3(origin);
    int stepX, stepY, stepZ;
    float tMaxX, tMaxY, tMaxZ, tDeltaX, tDeltaY, tDeltaZ, rayLength;

    if (direction.x > 0) {
      stepX = 1;
      tDeltaX = 1 / direction.x;
      tMaxX = (blockReference.blockX + 1 - origin.x) * tDeltaX;
    } else if (direction.x < 0) {
      stepX = -1;
      tDeltaX = 1 / -direction.x;
      tMaxX = (origin.x - blockReference.blockX) * tDeltaX;
    } else {
      stepX = 0;
      tDeltaX = 0;
      tMaxX = Float.MAX_VALUE;
    }

    if (direction.y > 0) {
      stepY = 1;
      tDeltaY = 1 / direction.y;
      tMaxY = (blockReference.blockY + 1 - origin.y) * tDeltaY;
    } else if (direction.y < 0) {
      stepY = -1;
      tDeltaY = 1 / -direction.y;
      tMaxY = (origin.y - blockReference.blockY) * tDeltaY;
    } else {
      stepY = 0;
      tDeltaY = 0;
      tMaxY = Float.MAX_VALUE;
    }

    if (direction.z > 0) {
      stepZ = 1;
      tDeltaZ = 1 / direction.z;
      tMaxZ = (blockReference.blockZ + 1 - origin.z) * tDeltaZ;
    } else if (direction.z < 0) {
      stepZ = -1;
      tDeltaZ = 1 / -direction.z;
      tMaxZ = (origin.z - blockReference.blockZ) * tDeltaZ;
    } else {
      stepZ = 0;
      tDeltaZ = 0;
      tMaxZ = Float.MAX_VALUE;
    }

    do {
      XYZ side;
      if (tMaxX < tMaxY) {
        if (tMaxX < tMaxZ) {
          rayLength = tMaxX;
          side = XYZ.x;
          blockReference.blockX += stepX;
          tMaxX = tMaxX + tDeltaX;
        } else {
          rayLength = tMaxZ;
          side = XYZ.z;
          blockReference.blockZ += stepZ;
          tMaxZ = tMaxZ + tDeltaZ;
        }
      } else {
        if (tMaxY < tMaxZ) {
          rayLength = tMaxY;
          side = XYZ.y;
          blockReference.blockY += stepY;
          tMaxY = tMaxY + tDeltaY;
        } else {
          rayLength = tMaxZ;
          side = XYZ.z;
          blockReference.blockZ += stepZ;
          tMaxZ = tMaxZ + tDeltaZ;
        }
      }

      if (rayLength < radius) {
        Block block = world.getBlock(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
        if (block != null) {
          BlockFace blockFace = null;
          if (side == XYZ.x) {
            if (stepX > 0) blockFace = BlockFace.negX;
            if (stepX < 0) blockFace = BlockFace.posX;
          } else if (side == XYZ.y) {
            if (stepY > 0) blockFace = BlockFace.negY;
            if (stepY < 0) blockFace = BlockFace.posY;
          } else if (side == XYZ.z) {
            if (stepZ > 0) blockFace = BlockFace.negZ;
            if (stepZ < 0) blockFace = BlockFace.posZ;
          }
          int blockMeta = world.getMeta(blockReference.blockX, blockReference.blockY, blockReference.blockZ);
          return new BlockIntersection(blockReference, blockFace, block, blockMeta, rayLength);
        }
      }
    } while (rayLength < radius);

    return null;
  }

  private enum XYZ {
    x, y, z
  }
}

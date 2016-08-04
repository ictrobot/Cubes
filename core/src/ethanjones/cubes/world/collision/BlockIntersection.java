package ethanjones.cubes.world.collision;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.storage.Area;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

import java.util.*;

import static java.lang.Math.floor;
import static java.lang.Math.signum;

public class BlockIntersection {

  private final BlockReference blockReference;
  private final BlockFace blockFace;
  private final Vector3 intersection;

  public BlockIntersection(BlockReference blockReference, BlockFace blockFace, Vector3 intersection) {
    this.blockReference = blockReference;
    this.blockFace = blockFace;
    this.intersection = intersection;
  }

  public BlockReference getBlockReference() {
    return blockReference;
  }

  public BlockFace getBlockFace() {
    return blockFace;
  }

  public Vector3 getIntersection() {
    return intersection;
  }

  public static BlockIntersection getBlockIntersection(Vector3 origin, Vector3 direction, World world) {
    return intersection(origin, direction, 6, world);
  }

  private static AreaReference fastGet = new AreaReference();
  private static BoundingBox boundingBox = new BoundingBox();
  private static Ray ray = new Ray();

  public static BlockIntersection intersection(Vector3 origin, Vector3 direction, int radius, World world) {
    Iterator<BlockReference> blocks = getSortedBlockList(origin, direction, radius, world);

    ray.set(origin, direction);
    Vector3 intersection = new Vector3();

    while (blocks.hasNext()) {
      BlockReference b = blocks.next();
      boundingBox.min.set(b.blockX, b.blockY, b.blockZ);
      boundingBox.max.set(b.blockX + 1, b.blockY + 1, b.blockZ + 1);

      if (Intersector.intersectRayBounds(ray, boundingBox, intersection)) {
        BlockFace blockFace = getBlockFace(b, intersection);
        return new BlockIntersection(b, blockFace, intersection);
      }
    }
    return null;
  }

  private static BlockFace getBlockFace(BlockReference b, Vector3 i) {
    i.sub(b.blockX, b.blockY, b.blockZ);
    if (i.x == 1f) return BlockFace.posX;
    if (i.x == 0f) return BlockFace.negX;
    if (i.y == 1f) return BlockFace.posY;
    if (i.y == 0f) return BlockFace.negY;
    if (i.z == 1f) return BlockFace.posZ;
    if (i.z == 0f) return BlockFace.negZ;
    return null;
  }

  private static Iterator<BlockReference> getSortedBlockList(final Vector3 origin, final Vector3 direction, int radius, World world) {
    int initialX = (int) floor(origin.x);
    int initialY = (int) floor(origin.y);
    int initialZ = (int) floor(origin.z);

    int stepX = (int) signum(direction.x);
    int stepY = (int) signum(direction.y);
    int stepZ = (int) signum(direction.z);

    final ArrayList<BlockReference> list = new ArrayList<BlockReference>();

    world.lock.readLock();
    int currentAreaX = Integer.MIN_VALUE;
    int currentAreaZ = Integer.MIN_VALUE;
    Area area = null;
    for (int nX = 0; nX <= (stepX != 0 ? radius : 0); nX++) {
      for (int nY = 0; nY <= (stepY != 0 ? radius : 0); nY++) {
        for (int nZ = 0; nZ <= (stepZ != 0 ? radius : 0); nZ++) {
          int x = initialX + (nX * stepX);
          int y = initialY + (nY * stepY);
          int z = initialZ + (nZ * stepZ);

          int areaX = CoordinateConverter.area(x);
          int areaZ = CoordinateConverter.area(z);

          if (areaX != currentAreaX || areaZ != currentAreaZ) {
            currentAreaX = areaX;
            currentAreaZ = areaZ;

            Area fastGet = fastGet(world, currentAreaX, currentAreaZ);
            if (area != null) area.lock.readUnlock();
            area = fastGet;
            if (area != null) area.lock.readLock();
          }

          if (area != null && area.isReady() && y <= area.maxY && y >= 0 && area.blocks[Area.getRef(x - area.minBlockX, y, z - area.minBlockZ)] != 0) {
            list.add(new BlockReference().setFromBlockCoordinates(x, y, z));
          }
        }
      }
    }
    if (area != null) area.lock.readUnlock();
    world.lock.readUnlock();

    return new Iterator<BlockReference>() {
      @Override
      public boolean hasNext() {
        return list.size() > 0;
      }

      @Override
      public BlockReference next() {
        BlockReference current = null;
        float currentDistance2 = Float.POSITIVE_INFINITY;
        for (BlockReference blockReference : list) {
          float distance2 = distance2(blockReference);
          if (distance2 < currentDistance2) {
            currentDistance2 = distance2;
            current = blockReference;
          }
        }
        list.remove(current);
        return current;
      }

      public float distance2(BlockReference b) {
        float dx = b.blockX - origin.x;
        float dy = b.blockY - origin.y;
        float dz = b.blockZ - origin.z;
        return (dx * dx) + (dy * dy) + (dz * dz);
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  private static Area fastGet(World world, int x, int z) {
    fastGet.setFromAreaCoordinates(x, z);
    return world.map.get(fastGet);
  }
}

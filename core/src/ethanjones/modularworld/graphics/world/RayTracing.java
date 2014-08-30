package ethanjones.modularworld.graphics.world;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.World;
import ethanjones.modularworld.world.reference.BlockReference;

public class RayTracing {

  public static BlockReference getIntersectionClient() {
    return getIntersection(new Ray(ModularWorldClient.instance.renderer.block.camera.position, ModularWorldClient.instance.renderer.block.camera.direction), ModularWorldClient.instance.world, 8);
  }

  public static BlockReference getIntersection(Ray ray, World world, int maxLength) {
    Vector3 tmp1 = ray.origin.cpy();
    Vector3 tmp2 = ray.getEndPoint(new Vector3(), maxLength);
    int x1 = (int) Math.floor(tmp1.x);
    int y1 = (int) Math.floor(tmp1.y);
    int z1 = (int) Math.floor(tmp1.z);
    int x2 = (int) Math.floor(tmp2.x);
    int y2 = (int) Math.floor(tmp2.y);
    int z2 = (int) Math.floor(tmp2.z);
    int startX = Math.min(x1, x2);
    int startY = Math.min(y1, y2);
    int startZ = Math.min(z1, z2);
    int endX = Math.max(x1, x2);
    int endY = Math.max(y1, y2);
    int endZ = Math.max(z1, z2);
    tmp2.set(1, 1, 1);
    for (int x = startX; x <= endX; x++) {
      for (int y = startY; y <= endY; y++) {
        for (int z = startZ; z <= endZ; z++) {
          Block block = world.getBlockFactory(x, y, z);
          if (block == null) continue;
          BoundingBox boundingBox = block.getBoundingBox();
          tmp1.set(x + 0.5f, y + 0.5f, z + 0.5f);
          if (boundingBox == null) {
            if (Intersector.intersectRayBoundsFast(ray, tmp1, tmp2)) {
              return new BlockReference().set(x, y, z);
            }
          } else if (Intersector.intersectRayBoundsFast(ray, tmp1, boundingBox.getDimensions())) {
            return new BlockReference().set(x, y, z);
          }
        }
      }
    }
    return null;
  }
}

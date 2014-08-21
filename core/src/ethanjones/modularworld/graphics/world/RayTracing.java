package ethanjones.modularworld.graphics.world;

import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.World;
import ethanjones.modularworld.world.reference.BlockReference;

public class RayTracing {

  public static BlockReference getBlock() {
    return getBlock(ModularWorldClient.instance.player.position, ModularWorldClient.instance.renderer.block.camera.direction, ModularWorldClient.instance.world, 8);
  }

  public static BlockReference getBlock(Vector3 starting, Vector3 direction, World world, int maxDistance) {
    Vector3 block = getVector(ModularWorldClient.instance.player.position, ModularWorldClient.instance.renderer.block.camera.direction, ModularWorldClient.instance.world, 8);
    if (block == null) return null;
    return new BlockReference().set((int) Math.floor(block.x), (int) Math.floor(block.y), (int) Math.floor(block.z));
  }

  public static Vector3 getVector(Vector3 starting, Vector3 direction, World world, int maxDistance) {
    Vector3 v = direction.cpy().nor();
    Vector3 pos = starting.cpy();
    for (int i = 0; i < maxDistance; i++) {
      if (world.getBlockFactory((int) Math.floor(pos.x), (int) Math.floor(pos.y), (int) Math.floor(pos.z)) != null) {
        return pos;
      }
      pos.add(v);
    }
    return null;
  }
}

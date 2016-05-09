package ethanjones.cubes.world.collision;

import ethanjones.cubes.core.event.EventHandler;
import ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;

import com.badlogic.gdx.math.Vector3;

public class PlayerCollision {
  private World world = Sided.getCubes().world;
  public static final float r = 0.25f;

  @EventHandler
  public void preventNoclip(PlayerMovementEvent event) {
    Player player = event.getPlayer();
    Vector3 pos = event.newPosition;
    if (world.getArea(CoordinateConverter.area(pos.x), CoordinateConverter.area(pos.z)) != null && !event.isCanceled()) {
      if (check(pos, 0f, 0f, 0f) || check(pos, 0f, -player.height, 0f)) {
        event.setCanceled(true);
        return;
      }

      limit(pos, +r, 0, player.height);
      limit(pos, -r, 0, player.height);
      limit(pos, 0, +r, player.height);
      limit(pos, 0, -r, player.height);
    }
  }

  private boolean check(Vector3 position, float xOffset, float yOffset, float zOffset) {
    int x = CoordinateConverter.block(position.x + xOffset);
    int y = CoordinateConverter.block(position.y + yOffset);
    int z = CoordinateConverter.block(position.z + zOffset);
    return world.getBlock(x, y, z) != null;
  }

  private void limit(Vector3 newPos, float xOffset, float zOffset, float height) {
    if (check(newPos, xOffset, 0f, zOffset) || check(newPos, xOffset, -height, zOffset)) {
      if (xOffset > 0) newPos.x = (float) (Math.floor(newPos.x + xOffset) - r);
      if (xOffset < 0) newPos.x = (float) (Math.floor(newPos.x + xOffset) + 1 + r);

      if (zOffset > 0) newPos.z = (float) (Math.floor(newPos.z + zOffset) - r);
      if (zOffset < 0) newPos.z = (float) (Math.floor(newPos.z + zOffset) + 1 + r);
    }
  }
}

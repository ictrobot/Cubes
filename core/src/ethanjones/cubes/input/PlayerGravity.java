package ethanjones.cubes.input;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;
import ethanjones.cubes.core.util.WorldGravity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;

public class PlayerGravity {
  private float time = 0f;
  private Vector3 temp;

  public Player player;
  public World world;

  public PlayerGravity(Player player, World world) {
    this.temp = new Vector3();
    this.player = player;
    this.world = world;
  }

  public void update() {
    if (player == null) player = Cubes.getClient().player;
    if (world == null) world = Cubes.getClient().world;
    Vector3 pos = player.position;

    if (world.getArea(CoordinateConverter.area(pos.x), CoordinateConverter.area(pos.z)) == null) {
      time = 0f;
      return;
    }
    temp.set(pos);

    float f = pos.y - player.height;
    int y = CoordinateConverter.block(f - 0.01f);
    if ((int) f == y && (f % 1) <= 0.1) y -= 1; // actually land on block
    Block b = world.getBlock(CoordinateConverter.block(pos.x), y, CoordinateConverter.block(pos.z));

    if (b == null) {
      float t = Gdx.graphics.getRawDeltaTime();
      float g = WorldGravity.playerGravity(time, t);

      time += t;
      temp.y -= g;
    } else {
      time = 0f;
      temp.y = y + 1 + player.height;
    }

    if (!temp.equals(pos)) {
      if (!new PlayerMovementEvent(Cubes.getClient().player, temp).post().isCanceled()) {
        pos.set(temp);
      }
    }
  }
}

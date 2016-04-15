package ethanjones.cubes.graphics.world;

import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.math.Vector3;

import java.util.Comparator;

import static ethanjones.cubes.world.storage.Area.HALF_SIZE_BLOCKS;
import static ethanjones.cubes.world.storage.Area.SIZE_BLOCKS;

public class AreaRendererSorter implements Comparator<AreaRenderer> {
  public Vector3 base = Cubes.getClient().player.position;

  @Override
  public int compare(AreaRenderer o1, AreaRenderer o2) {
    Vector3 v1 = o1.getOffset();
    Vector3 v2 = o2.getOffset();

    float d1 = base.dst2(v1.x + HALF_SIZE_BLOCKS, (o1.getYSection() * SIZE_BLOCKS) + HALF_SIZE_BLOCKS, v1.z + HALF_SIZE_BLOCKS);
    float d2 = base.dst2(v2.x + HALF_SIZE_BLOCKS, (o2.getYSection() * SIZE_BLOCKS) + HALF_SIZE_BLOCKS, v2.z + HALF_SIZE_BLOCKS);

    final float dst = d1 - d2;
    final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
    return result;
  }
}

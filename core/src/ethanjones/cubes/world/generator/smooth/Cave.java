package ethanjones.cubes.world.generator.smooth;

import ethanjones.cubes.world.reference.AreaReference;
import ethanjones.cubes.world.storage.Area;

import java.util.HashMap;

public class Cave {
  public final int caveStartX;
  public final int caveStartY;
  public final int caveStartZ;

  private final HashMap<AreaReference, int[]> blocks;

  public Cave(int x, int y, int z, HashMap<AreaReference, int[]> blocks) {
    this.caveStartX = x;
    this.caveStartY = y;
    this.caveStartZ = z;

    this.blocks = blocks;
  }

  public void apply(Area area) {
    int[] array = blocks.get(new AreaReference().setFromArea(area));
    if (array == null) return;
    for (int ref : array) {
      if (ref < area.blocks.length) area.blocks[ref] = 0;
    }
  }
}

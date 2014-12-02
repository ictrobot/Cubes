package ethanjones.cubes.world;

import static ethanjones.cubes.world.storage.Area.SIZE_BLOCKS;

public class CoordinateConverter {

  public static int block(float position) {
    return (int) Math.floor(position);
  }

  public static int area(float position) { //position or block
    return (int) Math.floor(position / SIZE_BLOCKS);
  }

}

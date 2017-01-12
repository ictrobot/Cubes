package ethanjones.cubes.world;

import static ethanjones.cubes.world.storage.Area.SIZE_BLOCKS;
import static ethanjones.cubes.world.storage.Area.SIZE_BLOCKS_POW2;

public class CoordinateConverter {

  public static int block(float position) {
    return (int) Math.floor(position);
  }

  public static int area(float position) { //position or block
    return (int) Math.floor(position / SIZE_BLOCKS);
  }
  
  public static int area(int position) {
    return position >> SIZE_BLOCKS_POW2;
  }

}

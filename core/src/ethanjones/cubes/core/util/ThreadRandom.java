package ethanjones.cubes.core.util;

import com.badlogic.gdx.math.RandomXS128;

public class ThreadRandom extends RandomXS128 {
  
  private static ThreadRandom random = new ThreadRandom();
  
  public static ThreadRandom get() {
    return random;
  }
}

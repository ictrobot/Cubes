package ethanjones.cubes.core.util;

import java.util.Random;

// Massive speed up in GWT by using Random instead of RandomXS128
public class ThreadRandom extends Random {
  
  private static ThreadRandom random = new ThreadRandom();
  
  public static ThreadRandom get() {
    return random;
  }
}

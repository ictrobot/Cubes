package ethanjones.cubes.core.util;

import com.badlogic.gdx.math.RandomXS128;

public class ThreadRandom extends RandomXS128 {
  
  private static ThreadLocal<ThreadRandom> local = new ThreadLocal<ThreadRandom>() {
    @Override
    protected ThreadRandom initialValue() {
      return new ThreadRandom();
    }
  };
  
  public static ThreadRandom get() {
    return local.get();
  }
}

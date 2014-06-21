package ethanjones.modularworld.core.util;

public class Maths {
  public static int fastPositive(int i) {
    if (i < 0) i = -i;
    return i;
  }
}

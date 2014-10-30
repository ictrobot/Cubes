package ethanjones.cubes.core.util;

public class LongAverage {

  private long data;
  private long total;
  private int num;

  public void add(long l) {
    data = l;
    total += l;
    num++;
  }

  public long getCurrent() {
    return data;
  }

  public long getAverage() {
    if (num == 0) {
      return 0;
    }
    return total / num;
  }
}

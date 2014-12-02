package ethanjones.cubes.block.data;

import java.util.Arrays;

public class IntegerAttribute extends BasicAttribute<Integer> {

  /**
   * @param min Inclusive
   * @param max Inclusive
   */
  public IntegerAttribute(String name, int min, int max) {
    this(name, min, max, 1);
  }

  /**
   * @param min Inclusive
   * @param max Inclusive
   */
  public IntegerAttribute(String name, int min, int max, int step) {
    super(name);
    if (step < 0) step = -step;
    for (int i = min; i <= max; i += step) {
      values.add(i);
    }
    unmodifiable();
  }

  public IntegerAttribute(String name, Integer... values) {
    super(name);
    this.values.addAll(Arrays.asList(values));
    unmodifiable();
  }
}

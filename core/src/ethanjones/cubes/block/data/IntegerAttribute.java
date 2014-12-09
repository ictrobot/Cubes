package ethanjones.cubes.block.data;

import com.badlogic.gdx.math.MathUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IntegerAttribute extends BasicAttribute<Integer> {

  private final boolean range;

  private final int min;
  private final int max;
  private final int step;

  private final List<Integer> values;

  public IntegerAttribute(String name, int min, int max) {
    this(name, min, max, 1);
  }

  /**
   * @param min Inclusive
   * @param max Inclusive
   */
  public IntegerAttribute(String name, int min, int max, int step) {
    super(name);
    this.range = true;
    this.min = min;
    this.max = max;
    this.step = step;
    this.values = null;
  }

  public IntegerAttribute(String name, Integer... values) {
    super(name);
    this.range = false;
    this.min = 0;
    this.max = 0;
    this.step = 0;
    if (values.length == 0) throw new IllegalArgumentException("No values!");
    ArrayList<Integer> list = new ArrayList<Integer>();
    list.addAll(Arrays.asList(values));
    this.values = Collections.unmodifiableList(list);
  }

  @Override
  public int getAttribute(Integer integer) {
    if (range) {
      return MathUtils.clamp(integer, min, max);
    } else {
      if (values.contains(integer)) return integer;
      return values.get(0);
    }
  }

  @Override
  public Integer getAttribute(int i) {
    if (range) {
      return MathUtils.clamp(i, min, max);
    } else {
      if (values.contains(i)) return i;
      return values.get(0);
    }
  }

  @Override
  public int getDefault() {
    return range ? min : values.get(0);
  }
}

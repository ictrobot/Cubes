package ethanjones.cubes.block.data;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EnumAttribute<T extends Enum<T>> extends BasicAttribute<T> {

  private final List<T> values;

  public EnumAttribute(String name, Class<T> tClass) {
    super(name);
    this.values = Collections.unmodifiableList(Arrays.asList(tClass.getEnumConstants()));
  }

  @Override
  public int getAttribute(T t) {
    return values.indexOf(t);
  }

  @Override
  public T getAttribute(int i) {
    return values.get(i);
  }

  @Override
  public int getDefault() {
    return 0;
  }
}

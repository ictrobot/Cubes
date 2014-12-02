package ethanjones.cubes.block.data;

import java.util.Arrays;

public class EnumAttribute<T extends Enum> extends BasicAttribute<T> {

  public EnumAttribute(String name, T... values) {
    super(name);
    this.values.addAll(Arrays.asList(values));
    unmodifiable();
  }
  
}

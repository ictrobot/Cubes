package ethanjones.cubes.block.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicAttribute<T> implements Attribute<T> {

  public final String name;
  public List<T> values;

  public BasicAttribute(String name) {
    this.name = name;
    this.values = new ArrayList<T>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public List<T> getValues() {
    return values;
  }

  protected void unmodifiable() {
    values = Collections.unmodifiableList(values);
  }
}

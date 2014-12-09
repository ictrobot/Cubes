package ethanjones.cubes.block.data;

public abstract class BasicAttribute<T> implements Attribute<T> {

  public final String name;

  public BasicAttribute(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }
}

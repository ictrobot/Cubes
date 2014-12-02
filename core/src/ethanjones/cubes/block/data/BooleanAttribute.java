package ethanjones.cubes.block.data;

public class BooleanAttribute extends BasicAttribute<Boolean> {
  
  public BooleanAttribute(String name) {
    super(name);
    values.add(false);
    values.add(true);
    unmodifiable();
  }
}

package ethanjones.cubes.block.data;

public class BooleanAttribute extends BasicAttribute<Boolean> {

  public BooleanAttribute(String name) {
    super(name);
  }

  @Override
  public int getAttribute(Boolean aBoolean) {
    return aBoolean ? 1 : 0;
  }

  @Override
  public Boolean getAttribute(int i) {
    return i != 0;
  }

  @Override
  public int getDefault() {
    return 0;
  }
}

package ethanjones.modularworld.core.settings;

public class IntegerSetting extends Setting<Integer> {

  private final Integer rangeStart;
  private final Integer rangeEnd;
  private final Integer step;
  private final boolean fixed;

  public IntegerSetting(SettingGroup parent, String name, Integer integer) {
    this(parent, name, integer, 0, 0, 0, true);
  }

  public IntegerSetting(SettingGroup parent, String name, Integer integer, Integer rangeStart, Integer rangeEnd, Integer step) {
    this(parent, name, integer, rangeStart, rangeEnd, step, false);
  }

  public IntegerSetting(SettingGroup parent, String name, Integer integer, Integer rangeStart, Integer rangeEnd, Integer step, boolean fixed) {
    super(parent, name, integer);
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
    this.step = step;
    this.fixed = fixed;
  }

  @Override
  public Integer next() {
    if (!fixed) {
      e = e + step;
      loop();
    }
    return e;
  }

  @Override
  public Integer previous() {
    if (!fixed) {
      e = e - step;
      loop();
    }
    return e;
  }

  private void loop() {
    if (e > rangeEnd) {
      e = rangeStart;
    } else if (e < rangeStart) {
      e = rangeEnd;
    }
  }

  @Override
  public String getString() {
    return e.toString();
  }

  @Override
  public void restore(String string) {
    try {
      e = Integer.valueOf(string);
    } catch (Exception e) {
      restoreFailed();
    }
  }
}

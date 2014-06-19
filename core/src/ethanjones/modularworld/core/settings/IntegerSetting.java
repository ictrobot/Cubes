package ethanjones.modularworld.core.settings;

public class IntegerSetting extends Setting<Integer> {

  private final Integer rangeStart;
  private final Integer rangeEnd;
  private final Integer step;

  public IntegerSetting(SettingGroup parent, String name, Integer integer, Integer rangeStart, Integer rangeEnd, Integer step) {
    super(parent, name, integer);
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
    this.step = step;
  }

  @Override
  public Integer next() {
    e = e + step;
    loop();
    return e;
  }

  @Override
  public Integer previous() {
    e = e - step;
    loop();
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

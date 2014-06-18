package ethanjones.modularworld.core.settings;

public class BooleanSetting extends Setting<Boolean> {

  public BooleanSetting(SettingGroup parent, String name, Boolean aBoolean) {
    super(parent, name, aBoolean);
  }

  @Override
  public Boolean next() {
    return e = !e;
  }

  @Override
  public Boolean previous() {
    return next();
  }

  @Override
  public String getString() {
    return e.toString();
  }

  @Override
  public void restore(String string) {
    if (string.contains("true")) {
      e = true;
    } else if (string.contains("false")) {
      e = false;
    } else {
      restoreFailed();
    }
  }
}

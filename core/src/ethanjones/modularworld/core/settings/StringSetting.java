package ethanjones.modularworld.core.settings;

public class StringSetting extends Setting<String> {

  public StringSetting(SettingGroup parent, String name, String s) {
    super(parent, name, s);
  }

  @Override
  public String next() {
    return getValue();
  }

  @Override
  public String previous() {
    return getValue();
  }

  @Override
  public String getString() {
    return getValue();
  }

  @Override
  public void restore(String string) {
    e = string;
  }
}

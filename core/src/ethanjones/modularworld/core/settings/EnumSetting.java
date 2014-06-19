package ethanjones.modularworld.core.settings;

public class EnumSetting<e extends Enum<e>> extends Setting<Enum<e>> {

  e[] constants;

  public EnumSetting(SettingGroup parent, String name, Enum<e> eEnum) {
    super(parent, name, eEnum);
    constants = e.getDeclaringClass().getEnumConstants();
  }

  @Override
  public Enum<e> next() {
    int index = e.ordinal();
    index++;
    if (index >= constants.length) {
      index = 0;
    }
    e = constants[index];
    return e;
  }

  @Override
  public Enum<e> previous() {
    int index = e.ordinal();
    index--;
    if (index < 0) {
      index = constants.length - 1;
    }
    e = constants[index];
    return e;
  }

  @Override
  public String getString() {
    return e.ordinal() + "";
  }

  @Override
  public void restore(String string) {
    int index = -1;
    try {
      index = Integer.valueOf(string);
    } catch (Exception e) {
      restoreFailed();
    }
    if (index >= 0 && index < constants.length) {
      e = constants[index];
    } else {
      restoreFailed();
    }
  }
}

package ethanjones.modularworld.core.settings;

import ethanjones.modularworld.core.events.setting.AfterProcessSettingEvent;
import ethanjones.modularworld.graphics.rendering.BlockRenderer;
import ethanjones.modularworld.side.common.ModularWorld;

public enum Settings {
  username(new StringSetting(null, null, "User")),
  renderer_block_viewDistance(new IntegerSetting(null, null, 1, BlockRenderer.RENDER_DISTANCE_MIN, BlockRenderer.RENDER_DISTANCE_MAX, 1)),
  networking_port(new IntegerSetting(null, null, 8080)),
  input_fieldOfView(new IntegerSetting(null, null, 67, 10, 120, 1));

  private final Setting setting;
  private String[] groupPath;
  private String name;

  private Settings(Setting setting) {
    this.setting = setting;
  }

  public static void processAll() {
    for (Enum e : Settings.class.getEnumConstants()) {
      ((Settings) e).process();
    }
    new AfterProcessSettingEvent().post();
  }

  public Setting getSetting() {
    SettingGroup group = ModularWorld.settings.main;
    for (String str : groupPath) {
      group = group.getSettingGroup(str);
    }
    return group.getSetting(name, setting);
  }

  public BooleanSetting getBooleanSetting() {
    return (BooleanSetting) getSetting();
  }

  public EnumSetting getEnumSetting() {
    return (EnumSetting) getSetting();
  }

  public IntegerSetting getIntegerSetting() {
    return (IntegerSetting) getSetting();
  }

  public StringSetting getStringSetting() {
    return (StringSetting) getSetting();
  }

  private void process() {
    String[] parts = this.name().split("_");
    name = parts[parts.length - 1];
    setting.name = name;
    groupPath = new String[parts.length - 1];
    int i = 0;
    SettingGroup group = ModularWorld.settings.main;
    while (i < parts.length - 1) {
      groupPath[i] = parts[i];
      group = group.getSettingGroup(parts[i]);
      i++;
    }
    setting.parent = group;
    setting.addToParent();
  }
}

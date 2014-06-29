package ethanjones.modularworld.core.settings;

import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.events.setting.AfterProcessSettingEvent;
import ethanjones.modularworld.graphics.rendering.BlockRenderer;

public enum Settings {
  renderer_block_viewDistance(new IntegerSetting(null, null, 1, BlockRenderer.RENDER_DISTANCE_MIN, BlockRenderer.RENDER_DISTANCE_MAX, 1));


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
    SettingGroup group = ModularWorld.instance.settings.main;
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

  private void process() {
    String[] parts = this.name().split("_");
    name = parts[parts.length - 1];
    setting.name = name;
    groupPath = new String[parts.length - 1];
    int i = 0;
    SettingGroup group = ModularWorld.instance.settings.main;
    while (i < parts.length - 1) {
      groupPath[i] = parts[i];
      group = group.getSettingGroup(parts[i]);
      i++;
    }
    setting.parent = group;
    setting.addToParent();
  }
}

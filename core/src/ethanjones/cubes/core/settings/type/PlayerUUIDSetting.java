package ethanjones.cubes.core.settings.type;

import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.UUID;

import static ethanjones.cubes.side.client.CubesClient.uuid;

public class PlayerUUIDSetting extends Setting {
  static {
    uuid = UUID.randomUUID();
  }

  @Override
  public boolean shouldDisplay() {
    return false;
  }

  @Override
  public Actor getActor(VisualSettingManager visualSettingManager) {
    return null;
  }

  @Override
  public String toString() {
    return uuid.toString();
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.put("uuid", uuid);
    return dataGroup;
  }

  @Override
  public void read(DataGroup dataGroup) {
    uuid = (UUID) dataGroup.get("uuid");
  }
}

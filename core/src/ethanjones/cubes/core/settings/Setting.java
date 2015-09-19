package ethanjones.cubes.core.settings;

import ethanjones.data.DataParser;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class Setting implements DataParser {

  public abstract Actor getActor(VisualSettingManager visualSettingManager);

  public abstract String toString();
}

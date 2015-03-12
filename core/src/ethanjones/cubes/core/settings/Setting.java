package ethanjones.cubes.core.settings;

import com.badlogic.gdx.scenes.scene2d.Actor;
import ethanjones.data.DataParser;

public abstract class Setting implements DataParser {

  public abstract Actor getActor(VisualSettingManager visualSettingManager);

  public abstract String toString();
}

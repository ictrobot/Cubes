package ethanjones.cubes.graphics.world;

import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;

import com.badlogic.gdx.math.Vector3;

public class RenderingSettings {
  
  protected int lightOverride = -1;
  protected boolean fogEnabled = true;
  
  public RenderingSettings setLightOverride(Vector3 position) {
    return setLightOverride(Cubes.getClient().world.getLightRaw(CoordinateConverter.block(position.x), CoordinateConverter.block(position.y), CoordinateConverter.block(position.z)));
  }
  
  public RenderingSettings setLightOverride(int value) {
    this.lightOverride = value;
    return this;
  }
  
  public RenderingSettings setFogEnabled(boolean value) {
    this.fogEnabled = value;
    return this;
  }
  
}

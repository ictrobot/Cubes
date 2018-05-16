package ethanjones.cubes.graphics;

import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.math.Vector3;

public class CubesRenderable extends Renderable {

  public String name = null;

  protected int lightOverride = -1;
  protected boolean fogEnabled = true;

  public CubesRenderable setLightOverride(Vector3 position) {
    return setLightOverride(Cubes.getClient().world.getLightRaw(CoordinateConverter.block(position.x), CoordinateConverter.block(position.y), CoordinateConverter.block(position.z)));
  }

  public CubesRenderable setLightOverride(int value) {
    this.lightOverride = value;
    return this;
  }

  public CubesRenderable setFogEnabled(boolean value) {
    this.fogEnabled = value;
    return this;
  }

  public CubesRenderable reset() {
    environment = null;
    material = null;
    meshPart.set("", null, 0, 0, 0);
    shader = null;
    userData = null;
    worldTransform.idt();

    name = null;
    lightOverride = -1;
    fogEnabled = true;

    return this;
  }
}

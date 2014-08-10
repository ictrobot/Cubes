package ethanjones.modularworld.graphics.world;

import com.badlogic.gdx.utils.Pool;

public class AreaRendererPool extends Pool<AreaRenderer> {
  @Override
  protected AreaRenderer newObject() {
    return new AreaRenderer();
  }
}

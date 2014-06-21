package ethanjones.modularworld.world;

import com.badlogic.gdx.utils.Pool;

public class AreaReferencePool extends Pool<AreaReference> {

  @Override
  protected AreaReference newObject() {
    return new AreaReference();
  }

  public AreaReference obtain() {
    return super.obtain().clear();
  }
}

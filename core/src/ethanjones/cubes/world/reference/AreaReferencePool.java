package ethanjones.cubes.world.reference;

import com.badlogic.gdx.utils.Pool;

public class AreaReferencePool extends Pool<AreaReference> {

  @Override
  protected AreaReference newObject() {
    return new AreaReference();
  }

}

package ethanjones.modularworld.world.reference;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;

public class AreaReferencePool extends Pool<AreaReference> {

  @Override
  protected synchronized AreaReference newObject() {
    return new AreaReference();
  }

  @Override
  public synchronized void free(AreaReference object) {
    super.free(object);
  }

  @Override
  public synchronized void freeAll(Array<AreaReference> objects) {
    super.freeAll(objects);
  }

  @Override
  public synchronized AreaReference obtain() {
    return super.obtain();
  }
}

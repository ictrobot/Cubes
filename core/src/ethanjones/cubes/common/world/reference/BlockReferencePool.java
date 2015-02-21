package ethanjones.cubes.common.world.reference;

import com.badlogic.gdx.utils.Pool;

public class BlockReferencePool extends Pool<BlockReference> {

  @Override
  protected BlockReference newObject() {
    return new BlockReference();
  }

}

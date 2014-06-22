package ethanjones.modularworld.graphics;

import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class GameBatch extends ModelBatch {

  public RenderablePool getRenderablePool() {
    return this.renderablesPool;
  }

}

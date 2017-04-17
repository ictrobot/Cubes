package ethanjones.cubes.graphics.world;

import com.badlogic.gdx.graphics.g3d.ModelBatch;

public class CubesModelBatch extends ModelBatch {
  
  public CubesModelBatch() {
    super(new WorldShaderProvider());
  }
}
package ethanjones.cubes.graphics.world;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;

import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;

import java.lang.reflect.Field;

public class CubesModelBatch extends ModelBatch {
  
  protected class CubesRenderablePool extends RenderablePool {
  
    @Override
    public Renderable obtain () {
      Renderable renderable = super.obtain();
      renderable.environment = null;
      renderable.material = null;
      renderable.meshPart.set("", null, 0, 0, 0);
      renderable.shader = null;
      
      // custom
      renderable.userData = null;
      renderable.worldTransform.idt();
      
      return renderable;
    }
  }
  
  public CubesModelBatch() {
    super(new WorldShaderProvider());
    
    try {
      Field field = ModelBatch.class.getDeclaredField("renderablesPool");
      field.setAccessible(true);
      field.set(this, new CubesRenderablePool());
      if (!(renderablesPool instanceof CubesRenderablePool)) throw new CubesException("Failed");
    } catch (Exception e) {
      Debug.crash(new CubesException("Failed to setup CubesRenderablePool", e));
    }
  }
}

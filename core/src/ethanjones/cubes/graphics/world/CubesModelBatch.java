package ethanjones.cubes.graphics.world;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.system.Debug;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.utils.RenderableSorter;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.lang.reflect.Field;
import java.util.Comparator;

public class CubesModelBatch extends ModelBatch {

  protected static class CubesRenderableSorter implements RenderableSorter, Comparator<Renderable> {
    // taken from DefaultRenderableSorter and modified

    private Camera camera;
    private final Vector3 translation1 = new Vector3();
    private final Vector3 translation2 = new Vector3();

    @Override
    public void sort(final Camera camera, final Array<Renderable> renderables) {
      this.camera = camera;
      renderables.sort(this);
    }

    private void getTranslation(Matrix4 worldTransform, Vector3 center, Vector3 output) {
      if (center.isZero()) {
        worldTransform.getTranslation(output);
      } else if (!worldTransform.hasRotationOrScaling()) {
        worldTransform.getTranslation(output).add(center);
      } else {
        output.set(center).mul(worldTransform);
      }
    }

    @Override
    public int compare(final Renderable o1, final Renderable o2) {
      if (o1.material == o2.material) {
        // when materials are the same, quicker path
        getTranslation(o1.worldTransform, o1.meshPart.center, translation1);
        getTranslation(o2.worldTransform, o2.meshPart.center, translation2);
        final float dst = (int) (1000f * camera.position.dst2(translation1)) - (int) (1000f * camera.position.dst2(translation2));
        return dst < 0 ? 1 : (dst > 0 ? -1 : 0);
      }

      final boolean b1 = o1.material.has(BlendingAttribute.Type) && ((BlendingAttribute) o1.material.get(BlendingAttribute.Type)).blended;
      final boolean b2 = o2.material.has(BlendingAttribute.Type) && ((BlendingAttribute) o2.material.get(BlendingAttribute.Type)).blended;
      if (b1 != b2) return b1 ? 1 : -1;

      getTranslation(o1.worldTransform, o1.meshPart.center, translation1);
      getTranslation(o2.worldTransform, o2.meshPart.center, translation2);
      final float dst = (int) (1000f * camera.position.dst2(translation1)) - (int) (1000f * camera.position.dst2(translation2));
      final int result = dst < 0 ? -1 : (dst > 0 ? 1 : 0);
      return b1 ? -result : result;
    }
  }

  protected static class CubesRenderablePool extends RenderablePool {

    @Override
    public Renderable obtain() {
      Renderable renderable = super.obtain();
      // super.obtain resets the following
      //renderable.environment = null;
      //renderable.material = null;
      //renderable.meshPart.set("", null, 0, 0, 0);
      //renderable.shader = null;

      // renderable.userData = null;
      // built in as of libgdx 1.9.6
      // https://github.com/libgdx/libgdx/pull/4550

      // custom
      renderable.worldTransform.idt();

      return renderable;
    }
  }

  public CubesModelBatch() {
    super(new WorldShaderProvider(), new CubesRenderableSorter());

    try {
      Field field = ModelBatch.class.getDeclaredField("renderablesPool");
      field.setAccessible(true);
      field.set(this, new CubesRenderablePool());
      field.setAccessible(false);
      if (!(renderablesPool instanceof CubesRenderablePool)) throw new CubesException("Not instance of CubesRenderablePool");
    } catch (Exception e) {
      Debug.crash(new CubesException("Failed to setup CubesRenderablePool", e));
    }
  }
}
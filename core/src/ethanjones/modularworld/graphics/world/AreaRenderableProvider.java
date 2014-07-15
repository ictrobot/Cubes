package ethanjones.modularworld.graphics.world;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class AreaRenderableProvider implements RenderableProvider, Pool.Poolable {

  private Future future;

  public AreaRenderableProvider setFuture(Future future) {
    this.future = future;
    return this;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    try {
      if (future != null) renderables.addAll((Array<Renderable>) future.get(0, TimeUnit.MICROSECONDS));
    } catch (ExecutionException e) {
      Log.error(new ModularWorldException("Failed to get renderables", e));
    } catch (Exception e) {

    }
  }

  @Override
  public void reset() {
    future = null;
  }

  public static class AreaRenderableProviderPool extends Pool<AreaRenderableProvider> {

    private Array<AreaRenderableProvider> allocated = new Array<AreaRenderableProvider>();

    @Override
    protected AreaRenderableProvider newObject() {
      return new AreaRenderableProvider();
    }

    @Override
    public AreaRenderableProvider obtain() {
      AreaRenderableProvider obtained = super.obtain();
      allocated.add(obtained);
      return obtained;
    }

    public void freeAll() {
      this.freeAll(allocated);
      allocated.clear();
    }
  }
}

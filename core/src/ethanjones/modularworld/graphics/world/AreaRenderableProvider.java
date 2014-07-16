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

public class AreaRenderableProvider implements RenderableProvider {

  public Future future;
  public final AreaRenderer areaRenderer;

  public AreaRenderableProvider(AreaRenderer areaRenderer) {
    this.areaRenderer = areaRenderer;
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    try {
      if (future != null && future.isDone())
        ((RenderableProvider) future.get(0, TimeUnit.MICROSECONDS)).getRenderables(renderables, pool);
    } catch (ExecutionException e) {
      Log.error(new ModularWorldException("Failed to get renderables", e));
    } catch (Exception e) {

    }
  }
}

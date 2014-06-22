package ethanjones.modularworld.world.rendering;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.world.storage.Area;

import static ethanjones.modularworld.world.storage.Area.SIZE_RENDER_AREA;

public class AreaRenderer implements RenderableProvider {

  public RenderArea[][][] renderAreas;
  private Array<Renderable> array;
  private boolean rebuildArray;

  public AreaRenderer(Area area) {
    renderAreas = new RenderArea[SIZE_RENDER_AREA][SIZE_RENDER_AREA][SIZE_RENDER_AREA];
    for (int rX = 0; rX < SIZE_RENDER_AREA; rX++) {
      for (int rY = 0; rY < SIZE_RENDER_AREA; rY++) {
        for (int rZ = 0; rZ < SIZE_RENDER_AREA; rZ++) {
          renderAreas[rX][rY][rZ] = new RenderArea(this);
        }
      }
    }

    array = new Array<Renderable>();
  }

  public void rebuildArray() {
    this.rebuildArray = true;
  }

  private void rebuild() {
    array.clear();
    for (int rX = 0; rX < SIZE_RENDER_AREA; rX++) {
      for (int rY = 0; rY < SIZE_RENDER_AREA; rY++) {
        for (int rZ = 0; rZ < SIZE_RENDER_AREA; rZ++) {
          array.addAll(renderAreas[rX][rY][rZ].data);
        }
      }
    }
  }


  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (rebuildArray) {
      rebuild();
      rebuildArray = false;
    }
    renderables.addAll(array);
  }
}

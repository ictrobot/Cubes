package ethanjones.modularworld.world.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.world.storage.Area;

import static ethanjones.modularworld.world.storage.Area.SIZE_RENDER_AREA;

public class AreaRenderer implements RenderableProvider {

  public RenderArea[][][] renderAreas;
  private Camera camera;
  //private Array<Renderable> array;
  //private boolean rebuildArray;

  public AreaRenderer(Area area) {
    renderAreas = new RenderArea[SIZE_RENDER_AREA][SIZE_RENDER_AREA][SIZE_RENDER_AREA];
    for (int rX = 0; rX < SIZE_RENDER_AREA; rX++) {
      for (int rY = 0; rY < SIZE_RENDER_AREA; rY++) {
        for (int rZ = 0; rZ < SIZE_RENDER_AREA; rZ++) {
          renderAreas[rX][rY][rZ] = new RenderArea(this);
        }
      }
    }
  }

  public AreaRenderer setCamera(Camera camera) {
    this.camera = camera;
    return this;
  }


  /**
   * public void rebuildArray() {
   * this.rebuildArray = true;
   * }
   * <p/>
   * private void rebuild() {
   * array.clear();
   * for (int rX = 0; rX < SIZE_RENDER_AREA; rX++) {
   * for (int rY = 0; rY < SIZE_RENDER_AREA; rY++) {
   * for (int rZ = 0; rZ < SIZE_RENDER_AREA; rZ++) {
   * array.addAll(renderAreas[rX][rY][rZ].data);
   * }
   * }
   * }
   * }
   */


  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    for (int rX = 0; rX < SIZE_RENDER_AREA; rX++) {
      for (int rY = 0; rY < SIZE_RENDER_AREA; rY++) {
        for (int rZ = 0; rZ < SIZE_RENDER_AREA; rZ++) {
          RenderArea renderArea = renderAreas[rX][rY][rZ];
          if (renderArea.inFrustum(camera.frustum)) renderables.addAll(renderArea.data);
        }
      }
    }
  }
}

package ethanjones.modularworld.world.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.graphics.GameModel;
import ethanjones.modularworld.world.storage.Area;

import java.util.HashMap;

public class RenderArea implements RenderableProvider {

  public static final int SIZE_BLOCKS = 8;
  public static final int HALF_SIZE_BLOCKS = SIZE_BLOCKS / 2;
  public HashMap<GameModel, Array<RenderPosition>> data;
  int cenBlockX;
  int cenBlockY;
  int cenBlockZ;

  public RenderArea() {
    data = new HashMap<GameModel, Array<RenderPosition>>();
  }

  public void render(ModelBatch modelBatch, Camera camera, Area area, int minBlockX, int minBlockY, int minBlockZ) {
    if (!isEmpty()) {
      return;
    }
    cenBlockX = minBlockX + HALF_SIZE_BLOCKS;
    cenBlockY = minBlockY + HALF_SIZE_BLOCKS;
    cenBlockZ = minBlockZ + HALF_SIZE_BLOCKS;
    for (int x = 0; x < SIZE_BLOCKS; x++) {
      for (int y = 0; y < SIZE_BLOCKS; y++) {
        for (int z = 0; z < SIZE_BLOCKS; z++) {
          Block b = area.getBlock(minBlockX + x, minBlockY + y, minBlockZ + z);
          if (b == null) continue;
          b.getRenderer().render(this, modelBatch, camera, minBlockX + x, minBlockY + y, minBlockZ + z);
        }
      }
    }
  }

  public void add(GameModel gameModel, RenderPosition renderPosition) {
    Array<RenderPosition> renderPositionArray = data.get(gameModel);
    if (renderPositionArray == null) {
      renderPositionArray = new Array<RenderPosition>(false, 128);
      data.put(gameModel, renderPositionArray);
    }
    renderPositionArray.add(renderPosition);
  }

  public void clear() {
    data.clear();
  }

  public boolean isEmpty() {
    return data.isEmpty();
  }

  @Override
  public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
    if (!ModularWorld.instance.renderer.block.camera.frustum.boundsInFrustum(cenBlockX, cenBlockY, cenBlockZ, HALF_SIZE_BLOCKS, HALF_SIZE_BLOCKS, HALF_SIZE_BLOCKS)) {
      return;
    }
    for (GameModel gameModel : data.keySet()) {
      for (RenderPosition renderPosition : data.get(gameModel)) {
        gameModel.transform.setToTranslation(renderPosition.x, renderPosition.y, renderPosition.z);
        gameModel.getRenderables(renderables, pool);
      }
    }
  }

  public static class RenderPosition {
    public final int x;
    public final int y;
    public final int z;

    public RenderPosition(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
  }
}

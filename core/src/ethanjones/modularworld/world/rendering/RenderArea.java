package ethanjones.modularworld.world.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.graphics.GameBatch;
import ethanjones.modularworld.graphics.GameModel;
import ethanjones.modularworld.world.storage.Area;

public class RenderArea {

  public static final int SIZE_BLOCKS = 8;
  public static final int HALF_SIZE_BLOCKS = SIZE_BLOCKS / 2;
  public static Pool<Renderable> pool;
  private static ShaderProvider shaderProvider;
  protected Array<Renderable> data;
  int cenBlockX;
  int cenBlockY;
  int cenBlockZ;
  private AreaRenderer parent;

  public RenderArea(AreaRenderer parent) {
    this.parent = parent;
    data = new Array<Renderable>();
  }

  public static void setup(GameBatch gameBatch) {
    RenderArea.pool = gameBatch.getRenderablePool();
    RenderArea.shaderProvider = gameBatch.getShaderProvider();
  }

  public void render(Camera camera, Area area, int minBlockX, int minBlockY, int minBlockZ) {
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
          b.getRenderer().render(this, camera, minBlockX + x, minBlockY + y, minBlockZ + z);
        }
      }
    }
    parent.rebuildArray();
  }

  public void add(GameModel gameModel) {
    final int offset = data.size;
    gameModel.getRenderables(data, pool);
    for (int i = offset; i < data.size; i++) {
      Renderable renderable = data.get(i);
      renderable.shader = shaderProvider.getShader(renderable);
    }
  }

  public void clear() {
    data.clear();
  }

  public boolean isEmpty() {
    return data.size == 0;
  }
}

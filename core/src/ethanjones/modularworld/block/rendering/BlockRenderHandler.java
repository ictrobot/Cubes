package ethanjones.modularworld.block.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import ethanjones.modularworld.world.rendering.RenderArea;

public interface BlockRenderHandler {

  /**
   * Called at factory loadGraphics();
   */
  public void load();

  /**
   * @return Number of faces rendered;
   */
  public void render(RenderArea modelBatch, ModelBatch environment, Camera camera, int x, int y, int z);

}

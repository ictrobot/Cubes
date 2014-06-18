package ethanjones.modularworld.block.rendering;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

public interface BlockRenderHandler {

  /**
   * Called at factory loadGraphics();
   */
  public void load();

  /**
   * @return Number of faces rendered;
   */
  public int render(ModelBatch modelBatch, Environment environment, Camera camera, int x, int y, int z);

}

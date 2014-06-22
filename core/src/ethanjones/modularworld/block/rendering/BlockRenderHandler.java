package ethanjones.modularworld.block.rendering;

import com.badlogic.gdx.graphics.Camera;
import ethanjones.modularworld.world.rendering.RenderArea;

public interface BlockRenderHandler {

  /**
   * Called at factory loadGraphics();
   */
  public void load();

  /**
   * @return Number of faces rendered;
   */
  public void render(RenderArea renderArea, Camera camera, int x, int y, int z);

}

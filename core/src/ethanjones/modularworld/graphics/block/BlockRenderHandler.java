package ethanjones.modularworld.graphics.block;

import com.badlogic.gdx.graphics.Camera;
import ethanjones.modularworld.graphics.world.RenderArea;

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

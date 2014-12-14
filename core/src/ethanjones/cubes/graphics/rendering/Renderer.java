package ethanjones.cubes.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

public class Renderer {

  public WorldRenderer worldRenderer;
  public GuiRenderer guiRenderer;

  public Renderer() {
    worldRenderer = new WorldRenderer();
    guiRenderer = new GuiRenderer();
  }

  public void render() {
    Gdx.gl20.glDisable(GL20.GL_BLEND);

    worldRenderer.render();
    guiRenderer.render();
  }

  public void dispose() {
    GLProfiler.disable();
    worldRenderer.dispose();
    guiRenderer.dispose();
  }

  public void resize() {
    guiRenderer.resize();
  }

  public boolean noCursorCatching() {
    return guiRenderer.noCursorCatching();
  }
}

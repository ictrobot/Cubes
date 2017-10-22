package ethanjones.cubes.graphics.rendering;

import ethanjones.cubes.graphics.world.ao.AmbientOcclusion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

public class Renderer {

  public WorldRenderer worldRenderer;
  public GuiRenderer guiRenderer;

  public Renderer() {
    worldRenderer = new WorldRenderer();
    guiRenderer = new GuiRenderer();

    AmbientOcclusion.load();
  }

  public void render() {
    Gdx.gl20.glDisable(GL20.GL_BLEND);

    worldRenderer.render();

    guiRenderer.render();
  }

  public void dispose() {
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

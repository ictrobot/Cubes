package ethanjones.cubes.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

import ethanjones.cubes.graphics.hud.HudRenderer;
import ethanjones.cubes.graphics.world.WorldRenderer;

public class Renderer {

  public WorldRenderer worldRenderer;
  public HudRenderer hudRenderer;

  public Renderer() {
    worldRenderer = new WorldRenderer();
    hudRenderer = new HudRenderer();
  }

  public void render() {
    Gdx.gl20.glDisable(GL20.GL_BLEND);

    worldRenderer.render();
    hudRenderer.render();
  }

  public void dispose() {
    GLProfiler.disable();
    worldRenderer.dispose();
    hudRenderer.dispose();
  }

  public void resize() {
    hudRenderer.resize();
  }

  public boolean noCursorCatching() {
    return hudRenderer.noCursorCatching();
  }
}

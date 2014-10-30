package ethanjones.cubes.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;

public class Renderer {

  public BlockRenderer block;
  public HudRenderer hud;

  public Renderer() {
    block = new BlockRenderer();
    hud = new HudRenderer();
  }

  public void render() {
    Gdx.gl20.glDisable(GL20.GL_BLEND);

    block.render();
    hud.render();
  }

  public void dispose() {
    GLProfiler.disable();
    block.dispose();
    hud.dispose();
  }

  public void resize() {
    hud.resize();
  }
}

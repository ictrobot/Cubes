package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import ethanjones.modularworld.core.logging.Log;

public class Renderer {

  public static boolean PROFILING = false;
  public int calls;
  public int drawCalls;
  public int shaderSwitches;
  public int textureBindings;

  public BlockRenderer block;
  public HudRenderer hud;

  public Renderer() {
    if (PROFILING) {
      GLProfiler.enable();
    }

    block = new BlockRenderer();
    hud = new HudRenderer();
  }

  public void render() {
    Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    Gdx.gl20.glDisable(GL20.GL_BLEND);

    block.render();
    hud.render();

    if (PROFILING) { //TODO Move to debug screen
      Log.debug("----------------------------------------");
      Log.debug("Calls:             " + GLProfiler.calls);
      Log.debug("DrawCalls:         " + GLProfiler.drawCalls);
      Log.debug("Shader Switches:   " + GLProfiler.shaderSwitches);
      Log.debug("Texture Bindings:  " + GLProfiler.textureBindings);
      Log.debug("DELTA");
      Log.debug("Calls:             " + (GLProfiler.calls - calls));
      Log.debug("DrawCalls:         " + (GLProfiler.drawCalls - drawCalls));
      Log.debug("Shader Switches:   " + (GLProfiler.shaderSwitches - shaderSwitches));
      Log.debug("Texture Bindings:  " + (GLProfiler.textureBindings - textureBindings));
      Log.debug("CURRENT");
      Log.debug("Vertex Count:      " + GLProfiler.vertexCount.latest);
      calls = GLProfiler.calls;
      drawCalls = GLProfiler.drawCalls;
      shaderSwitches = GLProfiler.shaderSwitches;
      textureBindings = GLProfiler.textureBindings;
    }
  }

  public void dispose() {
    GLProfiler.disable();
    block.dispose();
    hud.dispose();
  }

  public void resize() {
    block.setupCamera();
    hud.resize();
  }
}

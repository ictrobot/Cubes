package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.graphics.GameBatch;

public class Renderer {

  public static boolean PROFILING = false;
  public GameBatch gameBatch;
  public ModelBuilder modelBuilder;
  public BlockRenderer block;
  public HudRenderer hud;
  public int calls;
  public int drawCalls;
  public int shaderSwitches;
  public int textureBindings;

  public Renderer() {
    if (PROFILING) {
      GLProfiler.enable();
    }

    gameBatch = new GameBatch();
    modelBuilder = new ModelBuilder();

    block = new BlockRenderer(this);
    hud = new HudRenderer();
  }

  public void render() {
    long l = System.currentTimeMillis();

    Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    Gdx.gl20.glDisable(GL20.GL_BLEND);
    gameBatch.begin(block.camera);
    block.render();
    gameBatch.end();
    hud.render();

    long t = System.currentTimeMillis() - l;
    Debug.renderer(t);

    if (PROFILING) {
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
    gameBatch.dispose();
  }

  public void resize() {
    block.setupCamera();
    hud.resize();
  }
}

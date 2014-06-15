package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import ethanjones.modularworld.core.debug.Debug;

public class Renderer {
  
  public ModelBatch modelBatch;
  public ModelBuilder modelBuilder;
  
  public BlockRenderer block;
  public HudRenderer hud;
  
  public Renderer() {
    modelBatch = new ModelBatch();
    modelBuilder = new ModelBuilder();
    
    block = new BlockRenderer(this);
    hud = new HudRenderer();
  }
  
  public void render() {

      long l = System.currentTimeMillis();

    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    
    modelBatch.begin(block.camera);
    block.render();
    modelBatch.end();
    hud.render();

      long t = System.currentTimeMillis() - l;
      Debug.renderer(t);

      Debug.fps();
  }
  
  public void dispose() {
    modelBatch.dispose();
  }
  
  public void resize() {
    block.setupCamera();
    hud.resize();
  }
}

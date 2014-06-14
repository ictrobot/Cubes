package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

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
    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
    
    modelBatch.begin(block.camera);
    block.render();
    modelBatch.end();
    hud.render();
  }
  
  public void dispose() {
    modelBatch.dispose();
  }
  
  public void resize() {
    block.setupCamera();
    hud.resize();
  }
}

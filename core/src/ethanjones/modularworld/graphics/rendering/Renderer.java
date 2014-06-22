package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.graphics.GameBatch;

public class Renderer {

  public GameBatch gameBatch;
  public ModelBuilder modelBuilder;

  public BlockRenderer block;
  public HudRenderer hud;

  public Renderer() {
    gameBatch = new GameBatch();
    modelBuilder = new ModelBuilder();

    block = new BlockRenderer(this);
    hud = new HudRenderer();
  }

  public void render() {
    long l = System.currentTimeMillis();

    Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

    gameBatch.begin(block.camera);
    block.render();
    gameBatch.end();
    hud.render();

    long t = System.currentTimeMillis() - l;
    Debug.renderer(t);
  }

  public void dispose() {
    gameBatch.dispose();
  }

  public void resize() {
    block.setupCamera();
    hud.resize();
  }
}

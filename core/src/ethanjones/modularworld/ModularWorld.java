package ethanjones.modularworld;

import com.badlogic.gdx.ApplicationListener;
import ethanjones.modularworld.graphics.rendering.BasicRenderer;

public class ModularWorld implements ApplicationListener {
  
  BasicRenderer renderer;

  @Override
  public void create() {
    renderer = new BasicRenderer();
  }
  
  @Override
  public void resize(int width, int height) {
    renderer.setupCamera();
  }
  
  @Override
  public void render() {
    renderer.render();
  }
  
  @Override
  public void pause() {

  }
  
  @Override
  public void resume() {

  }
  
  @Override
  public void dispose() {
    renderer.dispose();
  }
}

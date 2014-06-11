package ethanjones.modularworld;

import com.badlogic.gdx.ApplicationListener;
import ethanjones.modularworld.block.BlockFactories;
import ethanjones.modularworld.graphics.rendering.Renderer;

public class ModularWorld implements ApplicationListener {
  
  public static ModularWorld instance;
  
  public Renderer renderer;
  
  public ModularWorld() {
    ModularWorld.instance = this;
  }

  @Override
  public void create() {
    BlockFactories.init();
    BlockFactories.loadTextures();
    renderer = new Renderer();
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

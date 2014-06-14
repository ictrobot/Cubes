package ethanjones.modularworld;

import com.badlogic.gdx.ApplicationListener;
import ethanjones.modularworld.block.BlockFactories;
import ethanjones.modularworld.graphics.rendering.Renderer;
import ethanjones.modularworld.world.World;
import ethanjones.modularworld.world.generator.BasicWorldGenerator;

public class ModularWorld implements ApplicationListener {
  
  public static ModularWorld instance;
  
  public World world;
  public Renderer renderer;
  
  public ModularWorld() {
    ModularWorld.instance = this;
  }
  
  @Override
  public void create() {
    BlockFactories.init();
    BlockFactories.loadTextures();
    
    world = new World(new BasicWorldGenerator());
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

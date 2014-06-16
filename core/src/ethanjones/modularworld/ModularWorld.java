package ethanjones.modularworld;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.block.BlockFactories;
import ethanjones.modularworld.graphics.rendering.Renderer;
import ethanjones.modularworld.input.InputChain;
import ethanjones.modularworld.world.World;
import ethanjones.modularworld.world.generator.BasicWorldGenerator;

public class ModularWorld implements ApplicationListener {

  public static ModularWorld instance;

  public World world;
  public Renderer renderer;
  public InputChain inputChain;
  public Player player;

  public ModularWorld() {
    ModularWorld.instance = this;
  }

  @Override
  public void create() {
    Gdx.app.log(Branding.NAME, Branding.DEBUG);

    player = new Player();

    BlockFactories.init();

    inputChain = new InputChain();

    renderer = new Renderer();
    BlockFactories.loadGraphics();

    world = new World(new BasicWorldGenerator());

    Gdx.input.setInputProcessor(inputChain.init());
    Gdx.input.setCursorCatched(true);
  }

  @Override
  public void resize(int width, int height) {
    renderer.resize();
  }

  @Override
  public void render() {
    inputChain.beforeRender();
    renderer.render();
    inputChain.afterRender();
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

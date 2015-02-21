package ethanjones.cubes.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import ethanjones.cubes.common.mod.ModManager;
import ethanjones.cubes.common.mod.event.StartingClientEvent;
import ethanjones.cubes.common.mod.event.StoppingClientEvent;
import ethanjones.cubes.platform.Adapter;
import ethanjones.cubes.common.CubesException;
import ethanjones.cubes.common.entity.living.player.Player;
import ethanjones.cubes.client.graphics.rendering.Renderer;
import ethanjones.cubes.client.input.InputChain;
import ethanjones.cubes.client.input.keyboard.KeyboardHelper;
import ethanjones.cubes.common.networking.NetworkingManager;
import ethanjones.cubes.common.Side;
import ethanjones.cubes.Cubes;
import ethanjones.cubes.common.world.client.WorldClient;

public class CubesClient extends Cubes implements ApplicationListener {

  public Player player;
  public InputChain inputChain;
  public Renderer renderer;

  public CubesClient() {
    super(Side.Client);
    if (Adapter.isDedicatedServer()) throw new CubesException("Cannot run client on server");
  }

  @Override
  public void create() {
    if (state.isSetup()) return;
    super.create();
    NetworkingManager.clientInit();

    inputChain = new InputChain();
    renderer = new Renderer();
    player = new Player(renderer.worldRenderer.camera);

    inputChain.setup();
    Gdx.input.setInputProcessor(InputChain.getInputMultiplexer());

    world = new WorldClient();

    ModManager.postModEvent(new StartingClientEvent());

    state.setup();
  }

  @Override
  public void render() {
    if (shouldReturn()) return;
    if (KeyboardHelper.isKeyDown(Input.Keys.ESCAPE)) {
      Adapter.gotoMainMenu();
      return;
    }
    super.render();
    inputChain.beforeRender();
    if (renderer.guiRenderer.isDebugEnabled()) ClientDebug.update();
    renderer.render();
    inputChain.afterRender();
    player.update();
  }

  @Override
  public void stop() {
    if (state.hasStopped() || !state.isSetup()) return;
    ModManager.postModEvent(new StoppingClientEvent());
    super.stop();
    renderer.dispose();
    inputChain.dispose();
  }

  @Override
  public void time(int interval) {
    if (shouldReturn()) return;
    super.time(interval);
  }

  @Override
  protected void tick() {
    super.tick();
    inputChain.cameraController.tick();
    ClientDebug.tick();
  }

  @Override
  public void resize(int width, int height) {
    if (shouldReturn()) return;
    renderer.resize();
  }

  @Override
  public void pause() {

  }

  @Override
  public void resume() {

  }
}

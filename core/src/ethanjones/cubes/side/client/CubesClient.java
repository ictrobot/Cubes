package ethanjones.cubes.side.client;

import ethanjones.cubes.core.performance.Performance;
import ethanjones.cubes.core.performance.PerformanceTags;
import ethanjones.cubes.core.mod.ModManager;
import ethanjones.cubes.core.mod.event.StartingClientEvent;
import ethanjones.cubes.core.mod.event.StoppingClientEvent;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.rendering.Renderer;
import ethanjones.cubes.input.InputChain;
import ethanjones.cubes.input.keyboard.KeyboardHelper;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.side.Side;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.client.WorldClient;
import ethanjones.cubes.world.collision.PlayerCollision;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import java.util.UUID;

public class CubesClient extends Cubes implements ApplicationListener {
  public static UUID uuid;

  public Player player;
  public InputChain inputChain;
  public Renderer renderer;
  public long frameStart;
  public float worldProgress = 0f;
  public boolean worldReady = false;

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
    Sided.getEventBus().register(new PlayerCollision());

    state.setup();
  }

  @Override
  public void render() {
    frameStart = System.nanoTime();
    if (shouldReturn()) return;
    if (worldReady) {
      Adapter.setMenu(null);
      worldReady = false;
    }
    if (KeyboardHelper.isKeyDown(Input.Keys.ESCAPE)) {
      Adapter.gotoMainMenu();
      return;
    }
    Performance.start(PerformanceTags.CLIENT_FRAME);
    super.render();
    inputChain.beforeRender();
    renderer.render();
    inputChain.afterRender();
    Performance.stop(PerformanceTags.CLIENT_FRAME);
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

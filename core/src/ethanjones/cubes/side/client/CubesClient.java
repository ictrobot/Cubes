package ethanjones.cubes.side.client;

import ethanjones.cubes.core.gwt.UUID;
import ethanjones.cubes.core.logging.Log;
import ethanjones.cubes.core.platform.Adapter;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.hud.inv.InventoryManager;
import ethanjones.cubes.graphics.menus.PauseMenu;
import ethanjones.cubes.graphics.menus.WorldLoadingMenu;
import ethanjones.cubes.graphics.rendering.Renderer;
import ethanjones.cubes.graphics.world.other.RainRenderer;
import ethanjones.cubes.input.InputChain;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.side.common.Side;
import ethanjones.cubes.world.client.WorldClient;
import ethanjones.cubes.world.collision.PlayerCollision;
import ethanjones.cubes.world.save.Gamemode;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

public class CubesClient extends Cubes implements ApplicationListener {

  public static UUID uuid;

  public Player player;
  public InputChain inputChain;
  public Renderer renderer;
  public Gamemode gamemode;
  public long frameStart;
  public float worldProgress = 0f;
  public boolean worldReady = false;
  private long nextTickTime;
  private int behindTicks;

  public CubesClient() {
    super(Side.Client);
    if (Adapter.isDedicatedServer()) throw new CubesException("Cannot run client on server");
  }

  @Override
  public void create() {
    if (state.isSetup()) return;
    super.create();

    inputChain = new InputChain();
    renderer = new Renderer();
    player = new Player(renderer.worldRenderer.camera);
    renderer.guiRenderer.playerCreated();

    inputChain.setup();
    Gdx.input.setInputProcessor(InputChain.getInputMultiplexer());

    world = new WorldClient();

    Side.getSidedEventBus().register(new PlayerCollision());

    ClientDebug.setup();

    nextTickTime = System.currentTimeMillis() + tickMS;

    state.setup();
  }

  @Override
  public void render() {
    frameStart = System.currentTimeMillis();
    if (shouldReturn()) return;
    if (worldReady) {
      Adapter.setMenu(null);
      worldReady = false;
    }
    if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) escape();

    ClientDebug.frameStart();

    long diff = nextTickTime - System.currentTimeMillis();
    if (diff < -16) {
      int change = 1 + (int) (-(diff + 16) / tickMS);
      behindTicks += change;
      nextTickTime += change * tickMS;
      diff = nextTickTime - System.currentTimeMillis();
    }
    if (diff < 2) {
      tick();
      nextTickTime += tickMS;
    } else if (behindTicks > 0) {
      tick();
      behindTicks--;
    }
    if (behindTicks >= (1000 / tickMS)) {
      Log.warning("Skipping " + behindTicks + " ticks");
      behindTicks = 0;
      nextTickTime += behindTicks * tickMS;
    }

    this.update();
    inputChain.beforeRender();
    renderer.render();
    inputChain.afterRender();
  }

  @Override
  protected void update() {
    super.update();
  }

  @Override
  protected void tick() {
    super.tick();
    inputChain.tick();
    RainRenderer.tick();
  }

  @Override
  public void stop() {
    if (state.hasStopped() || !state.isSetup()) return;
    super.stop();
    renderer.dispose();
    inputChain.dispose();
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

  public static void escape() {
    if (!(Adapter.getMenu() instanceof WorldLoadingMenu)) {
      if (InventoryManager.isInventoryOpen()) {
        InventoryManager.hideInventory();
      } else if (Adapter.getMenu() instanceof PauseMenu) {
        Adapter.setMenu(null);
      } else {
        Adapter.setMenu(new PauseMenu());
      }
    }
  }
}

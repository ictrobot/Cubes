package ethanjones.modularworld.side.client;

import com.badlogic.gdx.Gdx;
import ethanjones.modularworld.block.factory.BlockFactories;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.settings.Settings;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.graphics.rendering.Renderer;
import ethanjones.modularworld.input.InputChain;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.client.ClientNetworkingParameter;
import ethanjones.modularworld.side.Side;
import ethanjones.modularworld.side.client.debug.ClientDebug;
import ethanjones.modularworld.side.common.ModularWorld;
import ethanjones.modularworld.world.WorldClient;

public class ModularWorldClient extends ModularWorld {

  public static ModularWorldClient instance;
  private final ClientNetworkingParameter clientNetworkingParameter;

  public Player player;
  public InputChain inputChain;
  public Renderer renderer;

  public ModularWorldClient(ClientNetworkingParameter clientNetworkingParameter) {
    super(Side.Client);
    if (compatibility.isHeadless()) throw new ModularWorldException("Client requires Graphics ");
    this.clientNetworkingParameter = clientNetworkingParameter;
    ModularWorldClient.instance = this;
  }

  @Override
  public void create() {
    super.create();

    ClientDebug.setup();
    NetworkingManager.connectClient(clientNetworkingParameter);

    player = new Player(Settings.username.getStringSetting().getString());

    inputChain = new InputChain();
    renderer = new Renderer();
    BlockFactories.loadGraphics();

    inputChain.setup();
    Gdx.input.setInputProcessor(InputChain.getInputMultiplexer());
    Gdx.input.setCursorCatched(true);

    world = new WorldClient();
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    renderer.resize();
  }

  @Override
  public void render() {
    super.render();
    inputChain.beforeRender();
    ClientDebug.update();
    renderer.render();
    inputChain.afterRender();
  }

  @Override
  public void dispose() {
    renderer.dispose();
    super.dispose();
  }
}

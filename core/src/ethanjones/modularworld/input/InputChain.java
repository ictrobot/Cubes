package ethanjones.modularworld.input;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ethanjones.modularworld.side.client.debug.Debug;
import ethanjones.modularworld.core.hud.ChatManager;
import ethanjones.modularworld.input.keyboard.KeyboardHelper;
import ethanjones.modularworld.side.client.ModularWorldClient;

public class InputChain extends InputMultiplexer {

  public Stage hud;
  public ChatManager chatManager;
  public GameInputHandler game;

  public InputChain() {
    super();
  }

  public InputChain init() {
    this.addProcessor(chatManager = new ChatManager());
    this.addProcessor(KeyboardHelper.inputProcessor);
    this.addProcessor(hud);
    this.addProcessor(game = new GameInputHandler());
    return this;
  }

  public void beforeRender() {
    ModularWorldClient.instance.player.movementHandler.updatePosition();
    game.updateTouch();
    Debug.position();
  }

  public void afterRender() {
    ModularWorldClient.instance.player.movementHandler.afterRender();
  }
}

package ethanjones.modularworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import ethanjones.modularworld.core.hud.ChatManager;
import ethanjones.modularworld.input.keyboard.KeyboardHelper;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.client.debug.Debug;

public class InputChain {

  public Stage hud;
  public ChatManager chatManager;
  public GameInputHandler game;
  private static InputMultiplexer inputMultiplexer = new InputMultiplexer();

  static {
    Gdx.input.setInputProcessor(inputMultiplexer);
  }

  public static InputMultiplexer getInputMultiplexer() {
    return inputMultiplexer;
  }

  public void setup() {
    //Starts at top
    inputMultiplexer.addProcessor(chatManager = new ChatManager());
    inputMultiplexer.addProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.addProcessor(hud);
    inputMultiplexer.addProcessor(game = new GameInputHandler());
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

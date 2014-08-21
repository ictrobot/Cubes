package ethanjones.modularworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.input.keyboard.KeyboardHelper;
import ethanjones.modularworld.side.client.ModularWorldClient;

public class InputChain implements Disposable {

  public Stage hud;
  private static InputMultiplexer inputMultiplexer = new InputMultiplexer();

  static {
    Gdx.input.setInputProcessor(inputMultiplexer);
  }

  public static InputMultiplexer getInputMultiplexer() {
    return inputMultiplexer;
  }

  public void setup() {
    //Starts at top
    inputMultiplexer.addProcessor(hud);
    inputMultiplexer.addProcessor(KeyboardHelper.inputProcessor);
  }

  public void beforeRender() {
    ModularWorldClient.instance.player.movementHandler.update();
  }

  public void afterRender() {
    ModularWorldClient.instance.player.movementHandler.afterRender();
  }

  @Override
  public void dispose() {
    inputMultiplexer.removeProcessor(hud);
    inputMultiplexer.removeProcessor(KeyboardHelper.inputProcessor);
  }
}

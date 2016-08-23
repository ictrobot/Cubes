package ethanjones.cubes.input;

import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.input.keyboard.KeyboardHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

public class InputChain implements Disposable {

  private static InputMultiplexer inputMultiplexer = new InputMultiplexer();

  static {
    Gdx.input.setInputProcessor(inputMultiplexer);
  }

  public static InputMultiplexer getInputMultiplexer() {
    return inputMultiplexer;
  }

  public static void showMenu(Menu menu) {
    inputMultiplexer.addProcessor(0, menu.stage);
    if (menu.blockClientInput()) InputBlocker.BLOCK_INPUT = true;
  }

  public static void hideMenu(Menu menu) {
    inputMultiplexer.removeProcessor(menu.stage);
    InputBlocker.BLOCK_INPUT = false;
  }

  public Stage stageHud;
  public InputProcessor hud;
  public CameraController cameraController;


  public void setup() {
    //Starts at top
    inputMultiplexer.addProcessor(stageHud);
    inputMultiplexer.addProcessor(InputBlocker.INSTANCE);
    inputMultiplexer.addProcessor(hud);
    inputMultiplexer.addProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.addProcessor(cameraController);
  }

  public void beforeRender() {
    cameraController.update();
  }

  public void afterRender() {

  }

  @Override
  public void dispose() {
    inputMultiplexer.removeProcessor(stageHud);
    inputMultiplexer.removeProcessor(InputBlocker.INSTANCE);
    inputMultiplexer.removeProcessor(hud);
    inputMultiplexer.removeProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.removeProcessor(cameraController);
  }
}

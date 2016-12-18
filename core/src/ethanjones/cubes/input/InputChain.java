package ethanjones.cubes.input;

import ethanjones.cubes.core.platform.Compatibility;
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
  public DesktopController desktopController;
  public TouchController touchController;


  public void setup() {
    //Starts at top
    inputMultiplexer.addProcessor(stageHud);
    inputMultiplexer.addProcessor(InputBlocker.INSTANCE);
    inputMultiplexer.addProcessor(hud);
    inputMultiplexer.addProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.addProcessor(cameraController);
    if (Compatibility.get().isTouchScreen()) {
      touchController = new TouchController();
      inputMultiplexer.addProcessor(touchController);
      desktopController = null;
    } else {
      desktopController = new DesktopController();
      inputMultiplexer.addProcessor(desktopController);
      touchController = null;
    }
  }

  public void beforeRender() {
    cameraController.update();
    if (touchController != null) touchController.update();
  }

  public void afterRender() {

  }
  
  public void tick() {
    cameraController.tick();
    if (desktopController != null) desktopController.tick();
    if (touchController != null) touchController.tick();
  }

  @Override
  public void dispose() {
    inputMultiplexer.removeProcessor(stageHud);
    inputMultiplexer.removeProcessor(InputBlocker.INSTANCE);
    inputMultiplexer.removeProcessor(hud);
    inputMultiplexer.removeProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.removeProcessor(cameraController);
    if (desktopController != null) inputMultiplexer.removeProcessor(desktopController);
    if (touchController != null) inputMultiplexer.removeProcessor(touchController);
  }
}

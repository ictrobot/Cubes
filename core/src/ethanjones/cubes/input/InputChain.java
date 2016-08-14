package ethanjones.cubes.input;

import ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.input.keyboard.KeyboardHelper;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.gravity.WorldGravity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
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
  }

  public static void hideMenu(Menu menu) {
    inputMultiplexer.removeProcessor(menu.stage);
  }

  public Stage hud;
  public CameraController cameraController;

  public void setup() {
    //Starts at top
    inputMultiplexer.addProcessor(hud);
    inputMultiplexer.addProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.addProcessor(cameraController);
  }

  public void beforeRender() {
    cameraController.update();
    if (cameraController.jumpTime == 0 || cameraController.jumpTime >= CameraController.JUMP) {
      Player player = Cubes.getClient().player;
      Vector3 result = new Vector3();
      WorldGravity.doGravity(result, Cubes.getClient().world, player, Gdx.graphics.getRawDeltaTime());

      if (!result.equals(player.position) && !new PlayerMovementEvent(player, result).post().isCanceled()) {
        player.position.set(result);
      }
    }
  }

  public void afterRender() {

  }

  @Override
  public void dispose() {
    inputMultiplexer.removeProcessor(hud);
    inputMultiplexer.removeProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.removeProcessor(cameraController);
  }
}

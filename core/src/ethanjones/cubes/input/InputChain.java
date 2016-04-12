package ethanjones.cubes.input;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.input.keyboard.KeyboardHelper;
import ethanjones.cubes.side.Sided;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.CoordinateConverter;
import ethanjones.cubes.world.World;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
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
  public InputAdapter guiRendererTouch;

  public void setup() {
    //Starts at top
    inputMultiplexer.addProcessor(guiRendererTouch = new InputAdapter() {
      @Override
      public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return Cubes.getClient().renderer.guiRenderer.touch(screenX, screenY, pointer, button);
      }
    });
    inputMultiplexer.addProcessor(hud);
    inputMultiplexer.addProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.addProcessor(cameraController);
  }

  public void beforeRender() {
    cameraController.update();
    if (cameraController.jump == 0) playerGravity();
  }

  private void playerGravity() {
    World world = Cubes.getClient().world;
    Player player = Cubes.getClient().player;
    Vector3 pos = player.position.cpy();

    if (world.getArea(CoordinateConverter.area(pos.x), CoordinateConverter.area(pos.z)) == null)
      return;
    float f = pos.y - player.height;
    int y = CoordinateConverter.block(f - 0.01f);
    Block b = world.getBlock(CoordinateConverter.block(pos.x), y, CoordinateConverter.block(pos.z));
    if (b == null || f > y + 1.01f) {
      pos.y -= Math.max(6f * Gdx.graphics.getRawDeltaTime(), f - (y + 1));
    } else {
      pos.y = y + 1 + player.height;
    }
    if (!pos.equals(player.position)) {
      if (!new PlayerMovementEvent(pos.cpy()).post().isCanceled()) {
        player.position.set(pos);
      }
    }
  }

  public void afterRender() {

  }

  @Override
  public void dispose() {
    inputMultiplexer.removeProcessor(guiRendererTouch);
    inputMultiplexer.removeProcessor(hud);
    inputMultiplexer.removeProcessor(KeyboardHelper.inputProcessor);
    inputMultiplexer.removeProcessor(cameraController);
  }
}

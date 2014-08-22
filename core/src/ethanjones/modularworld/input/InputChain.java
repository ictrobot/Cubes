package ethanjones.modularworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import ethanjones.modularworld.input.keyboard.KeyboardHelper;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.packets.PacketClick;

public class InputChain implements Disposable {

  public Stage hud;
  public CameraController cameraController;
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
    inputMultiplexer.addProcessor(cameraController);
  }

  public void beforeRender() {
    PacketClick packet = new PacketClick();
    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
      packet.type = PacketClick.Click.get(Input.Buttons.LEFT);
    }
    if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
      packet.type = PacketClick.Click.get(Input.Buttons.MIDDLE);
    }
    if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
      packet.type = PacketClick.Click.get(Input.Buttons.RIGHT);
    }
    if (packet.type != null) {
      NetworkingManager.clientNetworking.sendToServer(packet);
    }
    cameraController.update();
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

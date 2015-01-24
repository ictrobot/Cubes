package ethanjones.cubes.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.IntIntMap;

import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketButton;
import ethanjones.cubes.networking.packets.PacketKey;
import ethanjones.cubes.networking.packets.PacketPlayerInfo;
import ethanjones.cubes.side.common.Cubes;

public class CameraController extends InputAdapter {

  private final Camera camera;
  private final IntIntMap keys = new IntIntMap();
  private final Vector3 tmp = new Vector3();
  public Touchpad touchpad; //movement on android
  private int STRAFE_LEFT = Input.Keys.A;
  private int STRAFE_RIGHT = Input.Keys.D;
  private int FORWARD = Input.Keys.W;
  private int BACKWARD = Input.Keys.S;
  private float speed = 5;
  private float degreesPerPixel = 0.5f;
  private Vector3 prevPosition = new Vector3();
  private Vector3 prevDirection = new Vector3();

  public CameraController(Camera camera) {
    this.camera = camera;
    camera.position.set(0, 6.5f, 0);
    camera.direction.set(1, 0, 0);
    camera.update();
  }

  @Override
  public boolean keyDown(int keycode) {
    keys.put(keycode, keycode);

    PacketKey packetKey = new PacketKey();
    packetKey.action = PacketKey.KEY_DOWN;
    packetKey.key = keycode;
    NetworkingManager.sendPacketToServer(packetKey);
    return true;
  }

  @Override
  public boolean keyUp(int keycode) {
    keys.remove(keycode, 0);
    PacketKey packetKey = new PacketKey();
    packetKey.action = PacketKey.KEY_UP;
    packetKey.key = keycode;
    NetworkingManager.sendPacketToServer(packetKey);
    return true;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    Cubes.getClient().renderer.guiRenderer.touch(screenX, screenY, pointer, button);

    PacketButton packetButton = new PacketButton();
    packetButton.action = PacketButton.BUTTON_DOWN;
    packetButton.button = button;
    NetworkingManager.sendPacketToServer(packetButton);
    return true;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    PacketButton packetButton = new PacketButton();
    packetButton.action = PacketButton.BUTTON_UP;
    packetButton.button = button;
    NetworkingManager.sendPacketToServer(packetButton);
    return true;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return mouseMoved(screenX, screenY);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    if (Cubes.getClient().renderer.guiRenderer.noCursorCatching()) return false;
    float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
    float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
    camera.direction.rotate(camera.up, deltaX);
    tmp.set(camera.direction).crs(camera.up).nor();
    camera.direction.rotate(tmp, deltaY);
    return true;
  }

  public void setSpeed(float speed) {
    this.speed = speed;
  }

  public void update() {
    if (Cubes.getClient().renderer.guiRenderer.noCursorCatching()) return;
    if (touchpad != null) {
      float knobPercentY = touchpad.getKnobPercentY();
      float up = knobPercentY > 0 ? knobPercentY : 0;
      float down = knobPercentY < 0 ? -knobPercentY : 0;

      float knobPercentX = touchpad.getKnobPercentX();
      float right = knobPercentX > 0 ? knobPercentX : 0;
      float left = knobPercentX < 0 ? -knobPercentX : 0;
      update(up, down, left, right);
    } else {
      update(keys.containsKey(FORWARD) ? 1f : 0f, keys.containsKey(BACKWARD) ? 1f : 0f, keys.containsKey(STRAFE_LEFT) ? 1f : 0f, keys.containsKey(STRAFE_RIGHT) ? 1f : 0f);
    }
  }

  private void update(float forward, float backward, float left, float right) {
    float deltaTime = Gdx.graphics.getRawDeltaTime();
    if (forward > 0) {
      tmp.set(camera.direction);
      tmp.nor().scl(deltaTime * speed * forward);
      camera.position.add(tmp);
    }
    if (backward > 0) {
      tmp.set(camera.direction);
      tmp.nor().scl(-deltaTime * speed * backward);
      camera.position.add(tmp);
    }
    if (left > 0) {
      tmp.set(camera.direction);
      tmp.crs(camera.up).nor().scl(-deltaTime * speed * left);
      camera.position.add(tmp);
    }
    if (right > 0) {
      tmp.set(camera.direction);
      tmp.crs(camera.up).nor().scl(deltaTime * speed * right);
      camera.position.add(tmp);
    }
    camera.update(true);
  }

  public void tick() {
    if (!Cubes.getClient().player.position.equals(prevPosition) || !Cubes.getClient().player.angle.equals(prevDirection)) {
      PacketPlayerInfo packetPlayerInfo = new PacketPlayerInfo();
      packetPlayerInfo.angle = Cubes.getClient().player.angle;
      packetPlayerInfo.position = Cubes.getClient().player.position;
      NetworkingManager.sendPacketToServer(packetPlayerInfo);
      prevPosition.set(Cubes.getClient().player.position);
      prevDirection.set(Cubes.getClient().player.angle);
    }
  }
}


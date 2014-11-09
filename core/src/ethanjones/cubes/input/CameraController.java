package ethanjones.cubes.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.utils.IntIntMap;

import ethanjones.cubes.side.client.CubesClient;
import ethanjones.cubes.world.WorldClient;

public class CameraController extends InputAdapter {

  private final Camera camera;
  private final IntIntMap keys = new IntIntMap();
  private final Vector3 tmp = new Vector3();
  private int STRAFE_LEFT = Input.Keys.A;
  private int STRAFE_RIGHT = Input.Keys.D;
  private int FORWARD = Input.Keys.W;
  private int BACKWARD = Input.Keys.S;
  //private int JUMP = Input.Keys.SPACE;
  //private float jumpCount = 0;
  private float speed = 5;
  private float degreesPerPixel = 0.5f;
  public Touchpad touchpad; //movement on android

  public CameraController(Camera camera) {
    this.camera = camera;
    camera.position.set(0, 6.5f, 0);
    camera.direction.set(1, 0, 0);
    camera.update();
  }

  @Override
  public boolean keyDown(int keycode) {
    keys.put(keycode, keycode);
    return true;
  }

  @Override
  public boolean keyUp(int keycode) {
    keys.remove(keycode, 0);
    return true;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return mouseMoved(screenX, screenY);
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
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
    update(Gdx.graphics.getDeltaTime());
  }

  public void update(float deltaTime) {
    if (touchpad != null) {
      float knobPercentY = touchpad.getKnobPercentY();
      float up = knobPercentY > 0 ? knobPercentY : 0;
      float down = knobPercentY < 0 ? -knobPercentY : 0;

      float knobPercentX = touchpad.getKnobPercentX();
      float right = knobPercentX > 0 ? knobPercentX : 0;
      float left = knobPercentX < 0 ? -knobPercentX : 0;

      update(1, up, down, left, right);
    } else {
      update(deltaTime, keys.containsKey(FORWARD) ? 1f : 0f, keys.containsKey(BACKWARD) ? 1f : 0f, keys.containsKey(STRAFE_LEFT) ? 1f : 0f, keys.containsKey(STRAFE_RIGHT) ? 1f : 0f);
    }
  }

  public void update(float deltaTime, float forward, float backward, float left, float right) {
    boolean moved = false;
    if (forward > 0) {
      tmp.set(camera.direction);
      tmp.nor().scl(deltaTime * speed * forward);
      camera.position.add(tmp);
      moved = true;
    }
    if (backward > 0) {
      tmp.set(camera.direction);
      tmp.nor().scl(-deltaTime * speed * backward);
      camera.position.add(tmp);
      moved = true;
    }
    if (left > 0) {
      tmp.set(camera.direction);
      tmp.crs(camera.up).nor().scl(-deltaTime * speed * left);
      camera.position.add(tmp);
      moved = true;
    }
    if (right > 0) {
      tmp.set(camera.direction);
      tmp.crs(camera.up).nor().scl(deltaTime * speed * right);
      camera.position.add(tmp);
      moved = true;
    }
    if (moved) ((WorldClient) CubesClient.instance.world).playerChangedPosition();
    camera.update(true);
  }
}


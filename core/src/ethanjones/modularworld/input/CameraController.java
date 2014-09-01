package ethanjones.modularworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

public class CameraController extends InputAdapter {
  private final Camera camera;
  private final IntIntMap keys = new IntIntMap();
  private final Vector3 tmp = new Vector3();
  private int STRAFE_LEFT = Input.Keys.A;
  private int STRAFE_RIGHT = Input.Keys.D;
  private int FORWARD = Input.Keys.W;
  private int BACKWARD = Input.Keys.S;
  private int JUMP = Input.Keys.SPACE;
  private float jumpCount = 0;
  private float speed = 5;
  private float degreesPerPixel = 0.5f;

  public CameraController(Camera camera) {
    this.camera = camera;
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

  public void setSpeed(float speed) {
    this.speed = speed;
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

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return mouseMoved(screenX, screenY);
  }

  public void update() {
    update(Gdx.graphics.getDeltaTime());
  }

  public void update(float deltaTime) {
    if (keys.containsKey(FORWARD)) {
      tmp.set(camera.direction);
      tmp.nor().scl(deltaTime * speed);
      camera.position.add(tmp);
    }
    if (keys.containsKey(BACKWARD)) {
      tmp.set(camera.direction);
      tmp.nor().scl(-deltaTime * speed);
      camera.position.add(tmp);
    }
    if (keys.containsKey(STRAFE_LEFT)) {
      tmp.set(camera.direction);
      tmp.crs(camera.up).nor().scl(-deltaTime * speed);
      camera.position.add(tmp);
    }
    if (keys.containsKey(STRAFE_RIGHT)) {
      tmp.set(camera.direction);
      tmp.crs(camera.up).nor().scl(deltaTime * speed);
      camera.position.add(tmp);
    }
    //if (keys.containsKey(JUMP) && jumpCount == 0) {
    //  jumpCount = 1.25f;
    //}
    //if (jumpCount > 0) {
    //camera.position.y += Math.min(jumpCount, deltaTime * 2);
    //  jumpCount -= deltaTime * 2;
    //  if (jumpCount < 0) jumpCount = 0;
    //}
    camera.update(true);
  }
}


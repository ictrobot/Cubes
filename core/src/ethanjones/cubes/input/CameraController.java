package ethanjones.cubes.input;

import ethanjones.cubes.core.event.entity.living.player.PlayerMovementEvent;
import ethanjones.cubes.core.settings.Keybinds;
import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketPlayerMovement;
import ethanjones.cubes.networking.packets.PacketThrowItem;
import ethanjones.cubes.side.common.Cubes;
import ethanjones.cubes.world.gravity.WorldGravity;
import ethanjones.cubes.world.save.Gamemode;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class CameraController extends InputAdapter {

  public static final float JUMP_START_VELOCITY = 5f;
  public static final float JUMP_RELEASE_VELOCITY = 2f;
  public static final float walkSpeed = 4.5f;
  public static final float flySpeed = 10f;
  
  private float degreesPerPixel = Settings.getFloatSettingValue(Settings.INPUT_MOUSE_SENSITIVITY) / 3;
  
  public Touchpad touchpad; //movement on android
  public ImageButton jumpButton;
  public ImageButton descendButton;
  
  private final Vector3 tmp = new Vector3();
  private final Vector3 tmpMovement = new Vector3();
  private Vector3 prevPosition = new Vector3();
  private Vector3 prevDirection = new Vector3();
  
  private final Camera camera;
  private boolean jumping = false;
  private boolean flying = false;
  private long lastJumpDown = 0;
  private boolean wasJumpDown = false;

  public CameraController(Camera camera) {
    this.camera = camera;
    camera.position.set(0, 6.5f, 0);
    camera.direction.set(1, 0, 0);
    camera.update();
  }
  
  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    if (Cubes.getClient().renderer.guiRenderer.noCursorCatching()) return false;
    float deltaX = -Gdx.input.getDeltaX(pointer) * degreesPerPixel;
    float deltaY = -Gdx.input.getDeltaY(pointer) * degreesPerPixel;
    
    tmpMovement.set(camera.direction);
    tmpMovement.rotate(camera.up, deltaX);
    tmp.set(tmpMovement).crs(camera.up).nor();
    tmpMovement.rotate(tmp, deltaY);
    
    if (preventFlicker(tmpMovement)) camera.direction.set(tmpMovement);
    return true;
  }
  
  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return touchDragged(screenX, screenY, 0);
  }
  
  private boolean preventFlicker(Vector3 newDirection) {
    float oldX = Math.signum(camera.direction.x);
    float oldZ = Math.signum(camera.direction.z);
    float newX = Math.signum(newDirection.x);
    float newZ = Math.signum(newDirection.z);
    
    return !(oldX != newX && oldZ != newZ);
  }
  
  @Override
  public boolean keyDown(int keycode) {
    return handled(keycode);
  }
  
  @Override
  public boolean keyUp(int keycode) {
    return handled(keycode);
  }
  
  private boolean handled(int keycode) {
    return keycode == Keybinds.getCode(Keybinds.KEYBIND_FORWARD) || keycode == Keybinds.getCode(Keybinds.KEYBIND_BACK) || keycode == Keybinds.getCode(Keybinds.KEYBIND_LEFT) || keycode == Keybinds.getCode(Keybinds.KEYBIND_RIGHT) || keycode == Keybinds.getCode(Keybinds.KEYBIND_JUMP) || keycode == Keybinds.getCode(Keybinds.KEYBIND_DESCEND);
  }
  
  public void update() {
    if (Cubes.getClient().renderer.guiRenderer.noCursorCatching()) {
      update(0f, 0f, 0f, 0f, false, false);
    } else if (touchpad != null) {
      float knobPercentY = touchpad.getKnobPercentY();
      float up = knobPercentY > 0 ? knobPercentY : 0;
      float down = knobPercentY < 0 ? -knobPercentY : 0;

      float knobPercentX = touchpad.getKnobPercentX();
      float right = knobPercentX > 0 ? knobPercentX : 0;
      float left = knobPercentX < 0 ? -knobPercentX : 0;
      update(up, down, left, right, jumpButton.getClickListener().isPressed(), descendButton.getClickListener().isPressed());
    } else {
      boolean forward = Keybinds.isPressed(Keybinds.KEYBIND_FORWARD);
      boolean back = Keybinds.isPressed(Keybinds.KEYBIND_BACK);
      boolean left = Keybinds.isPressed(Keybinds.KEYBIND_LEFT);
      boolean right = Keybinds.isPressed(Keybinds.KEYBIND_RIGHT);
      
      boolean up = Keybinds.isPressed(Keybinds.KEYBIND_JUMP);
      boolean desend = Keybinds.isPressed(Keybinds.KEYBIND_DESCEND);
      update(forward ? 1f : 0f, back ? 1f : 0f, left ? 1f : 0f, right ? 1f : 0f, up, desend);
    }
  }

  private void update(float forward, float backward, float left, float right, boolean jump, boolean descend) {
    float deltaTime = Gdx.graphics.getRawDeltaTime();
    if (deltaTime == 0f) return;
    float speed = flying() ? flySpeed : walkSpeed;
    tmpMovement.setZero();
    if (forward > 0) {
      tmp.set(camera.direction.x, 0, camera.direction.z).nor().nor().scl(deltaTime * speed * forward);
      tmpMovement.add(tmp);
    }
    if (backward > 0) {
      tmp.set(camera.direction.x, 0, camera.direction.z).nor().scl(-deltaTime * speed * backward);
      tmpMovement.add(tmp);
    }
    if (left > 0) {
      tmp.set(camera.direction.x, 0, camera.direction.z).crs(camera.up).nor().scl(-deltaTime * speed * left);
      tmpMovement.add(tmp);
    }
    if (right > 0) {
      tmp.set(camera.direction.x, 0, camera.direction.z).crs(camera.up).nor().scl(deltaTime * speed * right);
      tmpMovement.add(tmp);
    }
    tryMove();
    boolean onBlock = (!Cubes.getClient().player.noClip()) && WorldGravity.onBlock(Cubes.getClient().world, Cubes.getClient().player.position, Player.PLAYER_HEIGHT, Player.PLAYER_RADIUS);

    if (flying()) {
      if (jump) {
        tmpMovement.set(0, flySpeed * deltaTime, 0);
        tryMove();
      } else if (descend) {
        tmpMovement.set(0, -flySpeed * deltaTime, 0);
        tryMove();
      } else if (onBlock) {
        flying = false;
      }
    } else if (jumping) {
      if (!jump) {
        Cubes.getClient().player.motion.y = Math.min(JUMP_RELEASE_VELOCITY, Cubes.getClient().player.motion.y);
        jumping = false;
      }
    } else {
      if (jump && onBlock) {
        Cubes.getClient().player.motion.y = JUMP_START_VELOCITY;
        jumping = true;
      }
    }
    if (jump && !wasJumpDown && Cubes.getClient().gamemode == Gamemode.creative) {
      long time = System.currentTimeMillis();
      long delta = time - lastJumpDown;
      if (delta <= 500) {
        flying = !flying;
        lastJumpDown = 0;
      } else {
        lastJumpDown = time;
      }
    }
    wasJumpDown = jump;
    
    if (Cubes.getClient().player.motion.y <= 0) jumping = false;
    Cubes.getClient().player.updatePosition(deltaTime);
    
    if (Keybinds.isJustPressed(Keybinds.KEYBIND_THROW)) NetworkingManager.sendPacketToServer(new PacketThrowItem());

    camera.update(true);
  }

  private void tryMove() {
    if (tmpMovement.isZero()) return;
    tmpMovement.add(camera.position);
    if (!new PlayerMovementEvent(Cubes.getClient().player, tmpMovement).post().isCanceled()) {
      camera.position.set(tmpMovement);
    }
    tmpMovement.setZero();
  }

  public void tick() {
    Player player = Cubes.getClient().player;
    if (!player.position.equals(prevPosition) || !player.angle.equals(prevDirection)) {
      NetworkingManager.sendPacketToServer(new PacketPlayerMovement(player));
      prevPosition.set(player.position);
      prevDirection.set(player.angle);
    }
  }

  public boolean flying() {
    return flying || Cubes.getClient().player.noClip();
  }
}


package ethanjones.modularworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.core.events.entity.living.player.PlayerMovementEvent;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.input.keyboard.KeyboardHelper;

public class MovementHandler {

  public boolean touch = false;

  public Player player;
  public int deltaAngleX = 0;
  public int deltaAngleY = 0;
  Vector3 previousPos;

  public MovementHandler(Player player) {
    this.player = player;
    previousPos = player.position.cpy();
  }

  public void updateRotation() {
    updateRotation(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
  }

  public void updateRotation(int deltaX, int deltaY) {
    deltaAngleX = player.angleX;
    deltaAngleY = player.angleY;

    player.angleX += (double) -deltaX / Gdx.graphics.getWidth() * 360;
    player.angleY += (double) -deltaY / Gdx.graphics.getHeight() * 360;

    player.angleX = player.angleX % 360;
    player.angleY = Math.min(player.angleY, 360);
    player.angleY = Math.max(player.angleY, 0);

    deltaAngleX = player.angleX - deltaAngleX;
    deltaAngleY = player.angleY - deltaAngleY;

    Debug.facing();
  }

  public void afterRender() {
    deltaAngleX = 0;
    deltaAngleY = 0;
  }

  public void updateCamera(Camera camera) {
    camera.position.set(player.position.x, player.position.y, player.position.z);
    camera.direction.y = ((float) ModularWorld.instance.player.angleY - 180f) / 90f;
    camera.rotate(ModularWorld.instance.player.movementHandler.deltaAngleX, 0, 1, 0);
  }

  public void updatePosition() {
    if (KeyboardHelper.isKeyDown(Input.Keys.W) || touch) {
      ModularWorld.instance.player.position.add(ModularWorld.instance.renderer.world.camera.direction.cpy().nor());
    } else if (KeyboardHelper.isKeyDown(Input.Keys.S)) {
      ModularWorld.instance.player.position.sub(ModularWorld.instance.renderer.world.camera.direction.cpy().nor());
    }
    if (previousPos != player.position) {
      if (new PlayerMovementEvent().post()) {
        ModularWorld.instance.world.playerChangedPosition();
        previousPos = player.position.cpy();
      } else {
        player.position.set(previousPos);
      }
    }
  }
}

package ethanjones.modularworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.core.events.entity.living.player.PlayerMovementEvent;
import ethanjones.modularworld.entity.living.player.Player;

public class MovementHandler {

  public Player player;
  public int deltaAngleX = 0;
  public int deltaAngleY = 0;

  public MovementHandler(Player player) {
    this.player = player;
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
    if (new PlayerMovementEvent().post()) {
      //player.position.set(vector3);
      ModularWorld.instance.world.playerChangedPosition();
    }
  }
}

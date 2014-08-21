package ethanjones.modularworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.core.events.entity.living.player.PlayerMovementEvent;
import ethanjones.modularworld.entity.living.player.Player;
import ethanjones.modularworld.input.keyboard.KeyboardHelper;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.packets.PacketClick;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.world.WorldClient;

public class MovementHandler {

  public boolean touch = false;

  public Player player;
  public float deltaAngleX = 0;
  public float deltaAngleY = 0;
  Vector3 previousPos;

  public MovementHandler(Player player) {
    this.player = player;
    previousPos = player.position.cpy();
  }

  public void update() {
    updateClick();
    updateRotation(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
    updatePosition();
  }

  public void updateRotation(float deltaX, float deltaY) {
    deltaAngleX = player.angle.x;
    deltaAngleY = player.angle.y;

    player.angle.x += (double) -deltaX / Gdx.graphics.getWidth() * 360;
    player.angle.y += (double) -deltaY / Gdx.graphics.getHeight() * 360;

    player.angle.x = player.angle.x % 360;
    player.angle.y = Math.min(player.angle.y, 360);
    player.angle.y = Math.max(player.angle.y, 0);

    deltaAngleX = player.angle.x - deltaAngleX;
    deltaAngleY = player.angle.y - deltaAngleY;
  }

  public void afterRender() {
    deltaAngleX = 0;
    deltaAngleY = 0;
  }

  public void updateCamera(Camera camera) {
    camera.position.set(player.position.x, player.position.y, player.position.z);
    camera.direction.y = ((float) player.angle.y - 180f) / 90f;
    camera.rotate(player.movementHandler.deltaAngleX, 0, 1, 0);
  }

  public void updatePosition() {
    if (KeyboardHelper.isKeyDown(Input.Keys.W) || touch) {
      player.position.add(ModularWorldClient.instance.renderer.block.camera.direction.cpy().nor());
    } else if (KeyboardHelper.isKeyDown(Input.Keys.S)) {
      player.position.sub(ModularWorldClient.instance.renderer.block.camera.direction.cpy().nor());
    }
    if (!previousPos.equals(player.position)) {
      if (new PlayerMovementEvent().post()) {
        ((WorldClient) ModularWorldClient.instance.world).playerChangedPosition();
        previousPos = player.position.cpy();
      } else {
        player.position.set(previousPos);
      }
    }
  }

  public void updateClick() {
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
  }
}

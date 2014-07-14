package ethanjones.modularworld.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import ethanjones.modularworld.ModularWorld;

public class GameInputHandler extends InputAdapter {

  int[][] touch = new int[100][2];
  int maxPointer = -1;

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    maxPointer = Math.max(pointer, maxPointer);
    if (touch[pointer][0] != 0 && touch[pointer][1] != 0) {
      int deltaX = screenX - touch[pointer][0];
      int deltaY = screenY - touch[pointer][1];
      if (Math.abs(deltaX) <= 1 && Math.abs(deltaY) <= 1) {
        ModularWorld.instance.player.movementHandler.touch = true;
      } else {
        ModularWorld.instance.player.movementHandler.updateRotation(deltaX, deltaY);
      }
    }
    touch[pointer][0] = screenX;
    touch[pointer][1] = screenY;
    return true;
  }

  public void updateTouch() {
    for (int i = 0; i <= maxPointer; i++) {
      if (!Gdx.input.isTouched(i)) {
        touch[i][0] = 0;
        touch[i][1] = 0;
        ModularWorld.instance.player.movementHandler.touch = false;
      }
    }
    maxPointer = -1;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    ModularWorld.instance.player.movementHandler.updateRotation();
    return true;
  }

}

package ethanjones.modularworld.input;

import com.badlogic.gdx.InputAdapter;
import ethanjones.modularworld.ModularWorld;

public class GameInputHandler extends InputAdapter {
  
  @Override
  public boolean keyDown(int keycode) {
    
    return false;
  }
  
  @Override
  public boolean keyUp(int keycode) {
    
    return false;
  }
  
  int[][] touch = new int[100][2];
  
  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    int deltaX = screenX - touch[pointer][0];
    int deltaY = screenY - touch[pointer][1];
    ModularWorld.instance.player.updateRotation(deltaX, deltaY);
    touch[pointer][0] = screenX;
    touch[pointer][1] = screenY;
    return false;
  }
  
  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    System.out.println(screenX + " " + screenY);
    return false;
  }
  
}

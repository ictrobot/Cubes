package ethanjones.modularworld.input;

import com.badlogic.gdx.InputAdapter;

public class GameInputHandler extends InputAdapter {
  
  @Override
  public boolean keyDown(int keycode) {
    
    return false;
  }
  
  @Override
  public boolean keyUp(int keycode) {
    
    return false;
  }
  
  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    
    return false;
  }
  
  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    System.out.println(screenX + " " + screenY);
    return false;
  }
  
}

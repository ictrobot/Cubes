package ethanjones.modularworld;

import com.badlogic.gdx.Gdx;

public class Player {
  
  public int x = 10;
  public int y = 10;
  public int z = 10;
  
  public int angleX = 0;
  public int angleY = 0;
  
  public Player() {
    
  }
  
  public void updateRotation() {
    updateRotation(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
  }
  
  public void updateRotation(int deltaX, int deltaY) {
    angleX += (((double) deltaX) / Gdx.graphics.getWidth()) * 360;
    angleY += (((double) deltaY) / Gdx.graphics.getHeight()) * 360;
    
    angleX = angleX % 360;
    angleY = angleY % 360;
  }
  
}

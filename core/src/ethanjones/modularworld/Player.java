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
    angleX += (((double) Gdx.input.getDeltaX()) / Gdx.graphics.getWidth()) * 360;
    angleY += (((double) Gdx.input.getDeltaY()) / Gdx.graphics.getHeight()) * 360;
    
    angleX = angleX % 360;
    angleY = angleY % 360;
  }
  
}

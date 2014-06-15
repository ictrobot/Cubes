package ethanjones.modularworld;

import com.badlogic.gdx.Gdx;

public class Player {

  public int x = 0;
  public int y = 6;
  public int z = 0;

  public int angleX = 0;
  public int angleY = 180;

  public int deltaAngleX = 0;
  public int deltaAngleY = 0;

  public Player() {

  }

  public void updateRotation() {
    updateRotation(Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
  }

  public void updateRotation(int deltaX, int deltaY) {
    deltaAngleX = angleX;
    deltaAngleY = angleY;

    angleX += (double) deltaX / Gdx.graphics.getWidth() * 360;
    angleY += (double) -deltaY / Gdx.graphics.getHeight() * 360;

    angleX = angleX % 360;
    angleY = Math.min(angleY, 360);
    angleY = Math.max(angleY, 0);

    deltaAngleX = angleX - deltaAngleX;
    deltaAngleY = angleY - deltaAngleY;
  }

}

package ethanjones.modularworld.entity;

import com.badlogic.gdx.math.Vector3;

public class Entity {

  public Vector3 position;
  public int angleX;
  public int angleY;
  public boolean gravity = true;

  public Entity() {
    this.position = new Vector3(0, 6, 0);
    angleX = 0;
    angleY = 180;
  }

  /**
   * Must always call super
   */
  public void update() {

  }

}

package ethanjones.modularworld;

import com.badlogic.gdx.math.Vector3;
import ethanjones.modularworld.input.MovementHandler;

public class Player {

  public Vector3 position;

  public int angleX = 0;
  public int angleY = 180;

  public MovementHandler movementHandler;

  public Player() {
    this.movementHandler = new MovementHandler(this);
    this.position = new Vector3(0, 6, 0);
  }

}

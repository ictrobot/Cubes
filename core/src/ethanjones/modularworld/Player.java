package ethanjones.modularworld;

import ethanjones.modularworld.input.MovementHandler;

public class Player {

  public int x = 0;
  public int y = 6;
  public int z = 0;

  public int angleX = 0;
  public int angleY = 180;

  public MovementHandler movementHandler;

  public Player() {
    this.movementHandler = new MovementHandler(this);
  }

}

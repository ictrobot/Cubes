package ethanjones.modularworld.world.coordinates;

import com.badlogic.gdx.math.Vector3;

public class Coordinates extends BlockCoordinates {

  public final float x;
  public final float y;
  public final float z;

  public Coordinates(Vector3 position) {
    this(position.x, position.y, position.z);
  }

  public Coordinates(float x, float y, float z) {
    super(x, y, z);
    this.x = x;
    this.y = y;
    this.z = z;
  }
}

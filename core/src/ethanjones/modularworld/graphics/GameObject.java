package ethanjones.modularworld.graphics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GameObject extends ModelInstance {
  
  public final BoundingBox bounds = new BoundingBox();
  
  public GameObject(final Model model, float x, float y, float z) {
    super(model, x, y, z);
    calculateBoundingBox(bounds);
  }
}

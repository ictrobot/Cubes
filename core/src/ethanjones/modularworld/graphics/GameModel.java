package ethanjones.modularworld.graphics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;

public class GameModel extends ModelInstance {

  // public static int IDS_ALLOCATED = -1;
  // public static GameModel[] models = new GameModel[1024];

  public final BoundingBox bounds = new BoundingBox();
  int id;

  public GameModel(final Model model) {
    super(model);
    calculateBoundingBox(bounds);
    // id = IDS_ALLOCATED++;
    // models[id] = this;
  }

  public GameModel setPos(float x, float y, float z) {
    this.transform.setToTranslation(x, y, z);
    return this;
  }
}

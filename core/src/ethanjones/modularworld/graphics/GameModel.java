package ethanjones.modularworld.graphics;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class GameModel extends ModelInstance {

  int id;

  public GameModel(final Model model) {
    super(model);
  }

  public GameModel setPos(float x, float y, float z) {
    this.transform.setToTranslation(x, y, z);
    return this;
  }
}

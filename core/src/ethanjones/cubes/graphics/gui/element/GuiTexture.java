package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class GuiTexture extends TypeGuiElement<Texture> {

  public GuiTexture(Texture texture) {
    super(texture);
  }

  @Override
  public void render(Batch batch) {
    batch.draw(t, x.get(), y.get(), width.get(), height.get());
  }
}

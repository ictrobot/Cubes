package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GuiTextureRegion extends TypeGuiElement<TextureRegion> {

  public GuiTextureRegion(TextureRegion textureRegion) {
    super(textureRegion);
  }

  @Override
  public void render(Batch batch) {
    batch.draw(t, x.get(), y.get(), width.get(), height.get());
  }
}

package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GuiTextureRegion extends ResizableGuiElement {

  private TextureRegion textureRegion;

  public GuiTextureRegion(TextureRegion textureRegion) {
    this.textureRegion = textureRegion;
  }

  @Override
  public void render(Batch batch) {
    if (textureRegion != null) batch.draw(textureRegion, x.get(), y.get(), width.get(), height.get());
  }

  public TextureRegion getTextureRegion() {
    return textureRegion;
  }

  public void setTextureRegion(TextureRegion textureRegion) {
    this.textureRegion = textureRegion;
  }
}

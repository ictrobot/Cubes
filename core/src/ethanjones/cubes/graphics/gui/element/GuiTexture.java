package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class GuiTexture extends ResizableGuiElement {

  private Texture texture;

  public GuiTexture(Texture texture) {
    this.texture = texture;
  }

  @Override
  public void render(Batch batch) {
    if (texture != null) batch.draw(texture, x.get(), y.get(), width.get(), height.get());
  }

  public Texture getTexture() {
    return texture;
  }

  public void setTexture(Texture texture) {
    this.texture = texture;
  }
}

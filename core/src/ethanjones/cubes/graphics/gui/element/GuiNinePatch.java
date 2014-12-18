package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public class GuiNinePatch extends ResizableGuiElement {

  private NinePatch ninePatch;

  public GuiNinePatch(NinePatch ninePatch) {
    this.ninePatch = ninePatch;
  }

  @Override
  public void render(Batch batch) {
    ninePatch.draw(batch, x.get(), y.get(), width.get(), height.get());
  }

  public NinePatch getNinePatch() {
    return ninePatch;
  }

  public void setNinePatch(NinePatch ninePatch) {
    this.ninePatch = ninePatch;
  }
}

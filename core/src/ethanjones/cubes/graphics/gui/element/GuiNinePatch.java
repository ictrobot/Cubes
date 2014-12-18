package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public class GuiNinePatch extends TypeGuiElement<NinePatch> {

  public GuiNinePatch(NinePatch ninePatch) {
    super(ninePatch);
  }

  @Override
  public void render(Batch batch) {
    t.draw(batch, x.get(), y.get(), width.get(), height.get());
  }
}

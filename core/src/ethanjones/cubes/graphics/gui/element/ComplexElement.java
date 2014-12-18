package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import java.util.ArrayList;

import ethanjones.cubes.graphics.gui.element.event.GuiEvent;

public class ComplexElement extends GuiElement {

  protected ArrayList<GuiElement> elements = new ArrayList<GuiElement>();
  
  @Override
  public void render(Batch batch) {
    for (GuiElement guiElement : elements) {
      guiElement.render(batch);
    }
  }

  @Override
  public boolean fireEvent(GuiEvent guiEvent) {
    boolean r = super.fireEvent(guiEvent);
    for (GuiElement guiElement : elements) {
      boolean b = guiElement.fireEvent(guiEvent);
      r = r || b;
    }
    return r;
  }
}

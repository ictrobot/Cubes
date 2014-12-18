package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import java.util.ArrayList;

import ethanjones.cubes.graphics.gui.element.event.GuiEvent;
import ethanjones.cubes.graphics.gui.element.event.GuiEventListener;

public abstract class GuiElement {

  protected ArrayList<GuiEventListener> listeners = new ArrayList<GuiEventListener>();

  public abstract void render(Batch batch);

  public void addEventListener(GuiEventListener eventListener) {
    listeners.add(eventListener);
  }

  public void removeEventListener(GuiEventListener eventListener) {
    listeners.remove(eventListener);
  }

  public boolean fireEvent(GuiEvent guiEvent) {
    boolean r = false;
    for (GuiEventListener listener : listeners) {
      boolean b = listener.onEvent(guiEvent);
      r = r || b;
    }
    return r;
  }
}

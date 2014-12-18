package ethanjones.cubes.graphics.gui.element.event.key;

import ethanjones.cubes.graphics.gui.element.event.GuiEvent;

public class KeyEvent extends GuiEvent {

  public final int key;

  public KeyEvent(int key) {
    this.key = key;
  }
}

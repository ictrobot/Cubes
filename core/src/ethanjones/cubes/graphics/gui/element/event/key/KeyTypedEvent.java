package ethanjones.cubes.graphics.gui.element.event.key;

import ethanjones.cubes.graphics.gui.element.event.GuiEvent;

public class KeyTypedEvent extends GuiEvent {

  public final char character;

  public KeyTypedEvent(char character) {
    this.character = character;
  }
}

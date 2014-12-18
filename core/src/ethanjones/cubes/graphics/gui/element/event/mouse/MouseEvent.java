package ethanjones.cubes.graphics.gui.element.event.mouse;

import ethanjones.cubes.graphics.gui.element.event.GuiEvent;

public class MouseEvent extends GuiEvent {

  public final int x;
  public final int y;
  public final int button;

  public MouseEvent(int x, int y, int button) {
    this.x = x;
    this.y = y;
    this.button = button;
  }
}

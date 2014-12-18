package ethanjones.cubes.graphics.gui.element;

import ethanjones.cubes.graphics.gui.DynamicValue;

public abstract class ResizableGuiElement extends GuiElement {

  protected DynamicValue x = DynamicValue.zero();
  protected DynamicValue y = DynamicValue.zero();
  protected DynamicValue width = DynamicValue.zero();
  protected DynamicValue height = DynamicValue.zero();

  //SIZE
  public boolean setX(DynamicValue x) {
    this.x = x;
    return true;
  }

  public boolean setY(DynamicValue y) {
    this.y = y;
    return true;
  }

  public boolean setWidth(DynamicValue width) {
    this.width = width;
    return true;
  }

  public boolean setHeight(DynamicValue height) {
    this.height = height;
    return true;
  }

  public DynamicValue getX() {
    return x;
  }

  public DynamicValue getY() {
    return y;
  }

  public DynamicValue getWidth() {
    return width;
  }

  public DynamicValue getHeight() {
    return height;
  }

  public boolean inBounds(int x, int y) {
    if (x >= this.x.get() && x <= (this.x.get() + this.width.get())) {
      if (y >= this.y.get() && y <= (this.y.get() + this.height.get())) {
        return true;
      }
    }
    return false;
  }
}

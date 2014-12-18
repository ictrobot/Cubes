package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;

import ethanjones.cubes.graphics.gui.DynamicValue;
import ethanjones.cubes.graphics.gui.GuiElement;

public abstract class SimpleGuiElement implements GuiElement {

  protected DynamicValue x;
  protected DynamicValue y;
  protected DynamicValue width;
  protected DynamicValue height;

  public SimpleGuiElement() {
    this(DynamicValue.zero(), DynamicValue.zero(), DynamicValue.zero(), DynamicValue.zero());
  }

  public SimpleGuiElement(DynamicValue x, DynamicValue y, DynamicValue width, DynamicValue height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public abstract void render(Batch batch);

  @Override
  public boolean setX(DynamicValue x) {
    this.x = x;
    return true;
  }

  @Override
  public boolean setY(DynamicValue y) {
    this.y = y;
    return true;
  }

  @Override
  public boolean setWidth(DynamicValue width) {
    this.width = width;
    return true;
  }

  @Override
  public boolean setHeight(DynamicValue height) {
    this.height = height;
    return true;
  }

  @Override
  public DynamicValue getX() {
    return x;
  }

  @Override
  public DynamicValue getY() {
    return y;
  }

  @Override
  public DynamicValue getWidth() {
    return width;
  }

  @Override
  public DynamicValue getHeight() {
    return height;
  }

  @Override
  public boolean onButtonDown(int x, int y, int button) {
    return false;
  }

  @Override
  public boolean onButtonUp(int x, int y, int button) {
    return false;
  }

  @Override
  public boolean keyDown(int key) {
    return false;
  }

  @Override
  public boolean keyUp(int key) {
    return false;
  }

  @Override
  public boolean keyTyped(char key) {
    return false;
  }
}

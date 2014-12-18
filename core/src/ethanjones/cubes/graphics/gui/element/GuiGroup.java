package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import java.util.ArrayList;

import ethanjones.cubes.graphics.gui.DynamicValue;
import ethanjones.cubes.graphics.gui.GuiElement;

public class GuiGroup implements GuiElement {

  protected ArrayList<GuiElement> guiElements = new ArrayList<GuiElement>();
  
  @Override
  public void render(Batch batch) {
    for (GuiElement guiElement : guiElements) {
      guiElement.render(batch);
    }
  }

  @Override
  public boolean setX(DynamicValue x) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.setX(x);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean setY(DynamicValue y) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.setY(y);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean setWidth(DynamicValue width) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.setWidth(width);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean setHeight(DynamicValue height) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.setHeight(height);
      r = r || b;
    }
    return r;
  }

  @Override
  public DynamicValue getX() {
    return guiElements.size() > 0 ? guiElements.get(0).getX() : DynamicValue.zero();
  }

  @Override
  public DynamicValue getY() {
    return guiElements.size() > 0 ? guiElements.get(0).getY() : DynamicValue.zero();
  }

  @Override
  public DynamicValue getWidth() {
    return guiElements.size() > 0 ? guiElements.get(0).getWidth() : DynamicValue.zero();
  }

  @Override
  public DynamicValue getHeight() {
    return guiElements.size() > 0 ? guiElements.get(0).getHeight() : DynamicValue.zero();
  }

  @Override
  public boolean onButtonDown(int x, int y, int button) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.onButtonDown(x, y, button);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean onButtonUp(int x, int y, int button) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.onButtonUp(x, y, button);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean keyDown(int key) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.keyDown(key);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean keyUp(int key) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.keyUp(key);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean keyTyped(char key) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.keyTyped(key);
      r = r || b;
    }
    return r;
  }
}

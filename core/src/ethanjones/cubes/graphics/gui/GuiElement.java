package ethanjones.cubes.graphics.gui;

import com.badlogic.gdx.graphics.g2d.Batch;

public interface GuiElement {

  public void render(Batch batch);

  //SIZE
  public boolean setX(DynamicValue x);

  public boolean setY(DynamicValue y);

  public boolean setWidth(DynamicValue width);

  public boolean setHeight(DynamicValue height);

  public DynamicValue getX();

  public DynamicValue getY();

  public DynamicValue getWidth();

  public DynamicValue getHeight();

  //INPUT
  public boolean onButtonDown(int x, int y, int button);

  public boolean onButtonUp(int x, int y, int button);

  public boolean keyDown(int key);

  public boolean keyUp(int key);

  public boolean keyTyped(char key);
}

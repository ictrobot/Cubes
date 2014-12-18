package ethanjones.cubes.graphics.gui;

import com.badlogic.gdx.InputProcessor;
import java.util.ArrayList;

import ethanjones.cubes.graphics.gui.element.GuiElement;
import ethanjones.cubes.graphics.gui.element.ResizableGuiElement;
import ethanjones.cubes.graphics.gui.element.event.key.KeyDownEvent;
import ethanjones.cubes.graphics.gui.element.event.key.KeyTypedEvent;
import ethanjones.cubes.graphics.gui.element.event.key.KeyUpEvent;
import ethanjones.cubes.graphics.gui.element.event.mouse.MouseDownEvent;
import ethanjones.cubes.graphics.gui.element.event.mouse.MouseUpEvent;
import ethanjones.cubes.input.InputChain;

public class GuiMenu implements Menu, InputProcessor {

  protected ArrayList<GuiElement> elements = new ArrayList<GuiElement>();

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void render() {
    Gui.batch.begin();
    for (GuiElement guiElement : elements) {
      guiElement.render(Gui.batch);
    }
    Gui.batch.end();
  }

  public void hide() {
    InputChain.getInputMultiplexer().removeProcessor(this);
  }

  public void show() {
    InputChain.getInputMultiplexer().addProcessor(0, this);
  }

  @Override
  public boolean keyDown(int keycode) {
    boolean r = false;
    for (GuiElement guiElement : elements) {
      boolean b = guiElement.fireEvent(new KeyDownEvent(keycode));
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean keyUp(int keycode) {
    boolean r = false;
    for (GuiElement guiElement : elements) {
      boolean b = guiElement.fireEvent(new KeyUpEvent(keycode));
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean keyTyped(char character) {
    boolean r = false;
    for (GuiElement guiElement : elements) {
      boolean b = guiElement.fireEvent(new KeyTypedEvent(character));
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    boolean r = false;
    for (GuiElement guiElement : elements) {
      boolean b = false;
      if (guiElement instanceof ResizableGuiElement) {
        if (((ResizableGuiElement) guiElement).inBounds(screenX, screenY)) {
          b = guiElement.fireEvent(new MouseDownEvent(screenX, screenY, button));
        }
      } else {
        b = guiElement.fireEvent(new MouseDownEvent(screenX, screenY, button));
      }
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    boolean r = false;
    for (GuiElement guiElement : elements) {
      boolean b = guiElement.fireEvent(new MouseUpEvent(screenX, screenY, button));
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return false;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return false;
  }

  @Override
  public boolean scrolled(int amount) {
    return false;
  }
}

package ethanjones.cubes.graphics.gui;

import com.badlogic.gdx.InputProcessor;
import java.util.ArrayList;

import ethanjones.cubes.input.InputChain;

public class GuiMenu implements Menu, InputProcessor {

  protected ArrayList<GuiElement> guiElements = new ArrayList<GuiElement>();

  @Override
  public void resize(int width, int height) {

  }

  @Override
  public void render() {
    Gui.batch.begin();
    for (GuiElement guiElement : guiElements) {
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
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.keyDown(keycode);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean keyUp(int keycode) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.keyUp(keycode);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean keyTyped(char character) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.keyTyped(character);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.onButtonDown(screenX, screenY, button);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    boolean r = false;
    for (GuiElement guiElement : guiElements) {
      boolean b = guiElement.onButtonUp(screenX, screenY, button);
      r = r || b;
    }
    return r;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return true;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return true;
  }

  @Override
  public boolean scrolled(int amount) {
    return true;
  }
}

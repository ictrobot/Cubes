package ethanjones.cubes.input;

import com.badlogic.gdx.InputProcessor;

public class InputBlocker implements InputProcessor {
  protected static boolean BLOCK_INPUT = false;
  protected static InputBlocker INSTANCE = new InputBlocker();

  private InputBlocker() {
  }

  @Override
  public boolean keyDown(int keycode) {
    return BLOCK_INPUT;
  }

  @Override
  public boolean keyUp(int keycode) {
    return BLOCK_INPUT;
  }

  @Override
  public boolean keyTyped(char character) {
    return BLOCK_INPUT;
  }

  @Override
  public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    return BLOCK_INPUT;
  }

  @Override
  public boolean touchUp(int screenX, int screenY, int pointer, int button) {
    return BLOCK_INPUT;
  }

  @Override
  public boolean touchDragged(int screenX, int screenY, int pointer) {
    return BLOCK_INPUT;
  }

  @Override
  public boolean mouseMoved(int screenX, int screenY) {
    return BLOCK_INPUT;
  }

  @Override
  public boolean scrolled(int amount) {
    return BLOCK_INPUT;
  }
}

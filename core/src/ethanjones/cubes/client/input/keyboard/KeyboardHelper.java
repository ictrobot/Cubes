package ethanjones.cubes.client.input.keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;

public class KeyboardHelper {

  private static class KeyInputProcessor extends InputAdapter {

    @Override
    public boolean keyDown(int keycode) {
      for (KeyTypedListener listener : keyTypedListeners) {
        listener.keyDown(keycode);
      }
      return false;
    }

    @Override
    public boolean keyUp(int keycode) {
      for (KeyTypedListener listener : keyTypedListeners) {
        listener.keyUp(keycode);
      }
      return false;
    }

    @Override
    public boolean keyTyped(char character) {
      for (KeyTypedListener listener : keyTypedListeners) {
        listener.keyTyped(character);
      }
      return false;
    }
  }

  public static final InputProcessor inputProcessor = new KeyInputProcessor();
  private static Array<KeyTypedListener> keyTypedListeners = new Array<KeyTypedListener>();

  public static boolean isKeyUp(int keyCode) {
    return !isKeyDown(keyCode);
  }

  public static boolean isKeyDown(int keyCode) {
    return Gdx.input.isKeyPressed(keyCode);
  }

  public static void addKeyTypedListener(KeyTypedListener listener) {
    keyTypedListeners.add(listener);
  }

}

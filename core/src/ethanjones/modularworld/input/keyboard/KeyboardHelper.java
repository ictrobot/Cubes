package ethanjones.modularworld.input.keyboard;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import ethanjones.modularworld.core.logging.Log;

public class KeyboardHelper {

  public static final InputProcessor inputProcessor = new KeyInputProcessor();
  private static boolean[] keys;
  private static Array<KeyTypedListener> keyTypedListeners = new Array<KeyTypedListener>();

  static {
    int i = 255;
    try {
      for (Field f : ClassReflection.getFields(Input.Keys.class)) {
        Object o = f.get(null);
        if (!(o instanceof Integer)) continue;
        i = Math.max((Integer) o, i);
      }
    } catch (Exception e) {
      Log.info("Failed to get max keyCode", e);
    }
    keys = new boolean[i];
  }

  private static void setKeyDown(int keyCode) {
    keys[keyCode] = true;
  }

  private static void setKeyUp(int keyCode) {
    keys[keyCode] = false;
  }

  public static boolean isKeyDown(int keyCode) {
    return Gdx.input.isKeyPressed(keyCode);
  }

  public static boolean isKeyUp(int keyCode) {
    return !isKeyDown(keyCode);
  }

  public static boolean[] getKeyStates() {
    return keys;
  }

  public static void addKeyTypedListener(KeyTypedListener listener) {
    keyTypedListeners.add(listener);
  }

  private static class KeyInputProcessor extends InputAdapter {
    @Override
    public boolean keyDown(int keycode) {
      setKeyDown(keycode);
      for (KeyTypedListener listener : keyTypedListeners) {
        listener.keyDown(keycode);
      }
      return false;
    }

    @Override
    public boolean keyUp(int keycode) {
      setKeyUp(keycode);
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

}

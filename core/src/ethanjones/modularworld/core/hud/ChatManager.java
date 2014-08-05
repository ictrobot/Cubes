package ethanjones.modularworld.core.hud;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ChatManager extends InputAdapter {

  private static String string = "";
  private static Chat chat;
  protected static boolean visible = true;

  @Override
  public boolean keyTyped(char character) {
    if (!visible) return false;
    if (character == '\b') {
      if (string.length() >= 1) string = string.substring(0, string.length() - 1);
    } else {
      string = string + character;
    }
    if (chat != null) chat.string = string;
    return true;
  }

  @Override
  public boolean keyDown(int keycode) {
    return visible;
  }

  @Override
  public boolean keyUp(int keycode) {
    return visible;
  }

  public static Chat getChat(Skin skin) {
    chat = new Chat(skin);
    return chat;
  }
}

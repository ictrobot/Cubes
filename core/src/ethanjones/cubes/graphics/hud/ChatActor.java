package ethanjones.cubes.graphics.hud;

import ethanjones.cubes.core.util.Toggle;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.graphics.menu.Menu;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;

import java.util.ArrayDeque;

public class ChatActor extends ScrollPane {

  private static final int MAX_OLD_MESSAGES = 500;
  private static final int MESSAGES_DISPLAY = 7;

  private ArrayDeque<ChatMessage> history = new ArrayDeque<ChatMessage>();
  private ArrayDeque<ChatMessage> current = new ArrayDeque<ChatMessage>();
  private SimpleListActor<ChatMessage> listActor = new SimpleListActor<ChatMessage>(Fonts.hud);

  public Toggle open = new Toggle(false) {
    @Override
    protected void doEnable() {
      listActor.setCollection(history);
      getStage().setScrollFocus(ChatActor.this);
      setScrollingDisabled(true, false);
      layout();
      setScrollY(getMaxY());
      updateVisualScroll();
    }

    @Override
    protected void doDisable() {
      listActor.setCollection(current);
      getStage().setScrollFocus(null);
      setScrollingDisabled(true, true);
      layout();
      setScrollY(getMaxY());
      updateVisualScroll();
    }
  };
  private boolean newMessages = false;

  public ChatActor() {
    super(null, Menu.skin);
    setWidget(listActor);
    listActor.setGap(4f);
  }

  @Override
  public void act(float delta) {
    if (open.isEnabled()) {
      if (newMessages) {
        listActor.invalidate();
        newMessages = false;
      }
    } else {
      long oldestToDisplay = System.currentTimeMillis() - (10 * 1000);
      boolean updated = newMessages;
      while (current.peekLast() != null && current.peekLast().timeReceived < oldestToDisplay) {
        current.removeLast();
        updated = true;
      }
      if (updated) {
        listActor.invalidate();
        newMessages = false;
      }
    }
    super.act(delta);
  }

  public void newMessage(String str) {
    ChatMessage chatMessage = new ChatMessage(str);

    history.addFirst(chatMessage);
    trim(history, MAX_OLD_MESSAGES);

    current.addFirst(chatMessage);
    trim(current, MESSAGES_DISPLAY);

    newMessages = true;
  }

  @Override
  public float getPrefHeight() {
    return listActor.getItemHeight() * MESSAGES_DISPLAY;
  }

  private void trim(ArrayDeque<ChatMessage> messages, int maxLength) {
    while (messages.size() > maxLength) {
      messages.removeLast();
    }
  }

  private static class ChatMessage {
    final String msg;
    final long timeReceived;

    ChatMessage(String msg) {
      this.msg = msg;
      this.timeReceived = System.currentTimeMillis();
    }

    @Override
    public String toString() {
      return msg;
    }
  }
}

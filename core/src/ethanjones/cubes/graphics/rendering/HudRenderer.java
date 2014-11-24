package ethanjones.cubes.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.ArrayList;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.input.keyboard.KeyTypedAdapter;
import ethanjones.cubes.input.keyboard.KeyboardHelper;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.side.client.ClientDebug;
import ethanjones.cubes.side.common.Cubes;

import static ethanjones.cubes.graphics.menu.Menu.skin;

public class HudRenderer implements Disposable {

  private class KeyListener extends KeyTypedAdapter {

    final int debug = Input.Keys.F1;
    final int chat = Input.Keys.F2;

    @Override
    public void keyDown(int keycode) {
      if (keycode == debug) {
        setDebugEnabled(!isDebugEnabled());
      }
      if (keycode == chat) {
        setChatEnabled(!isChatEnabled());
      }
    }
  }

  Stage stage;
  TextField chat;
  Label chatLog;
  ArrayList<String> chatStrings = new ArrayList<String>();
  Image crosshair;
  Touchpad touchpad;
  TextButton debugButton;
  TextButton chatButton;
  ClientDebug.DebugLabel debug;
  KeyListener keyListener;
  private boolean chatEnabled;
  private boolean debugEnabled;

  public HudRenderer() {
    stage = new Stage(new ScreenViewport());
    Cubes.getClient().inputChain.hud = stage;

    keyListener = new KeyListener();
    KeyboardHelper.addKeyTypedListener(keyListener);

    crosshair = new Image(Assets.getTextureRegion("core:hud/Crosshair.png"));

    debug = new ClientDebug.DebugLabel();

    TextField.TextFieldStyle defaultStyle = skin.get("default", TextField.TextFieldStyle.class);
    TextField.TextFieldStyle chatStyle = new TextField.TextFieldStyle(defaultStyle);
    chatStyle.font = Fonts.Size2;
    chatStyle.background = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/ChatBackground.png"));

    chat = new TextField("", chatStyle);
    chat.setTextFieldListener(new TextField.TextFieldListener() {
      @Override
      public void keyTyped(TextField textField, char c) {
        if (c == '\n' || c == '\r') {
          PacketChat packetChat = new PacketChat();
          packetChat.msg = chat.getText();
          NetworkingManager.sendPacketToServer(packetChat);
          chat.setText("");
          setChatEnabled(false);

        }
      }
    });
    chatLog = new Label("", new LabelStyle(Fonts.Size2, Color.WHITE));
    chatLog.setAlignment(Align.bottomLeft, Align.left);

    if (Compatibility.get().isTouchScreen()) {
      touchpad = new Touchpad(10f, skin);

      Cubes.getClient().inputChain.cameraController.touchpad = touchpad;

      debugButton = new TextButton(Localization.get("hud.debug"), skin, "tiny");
      debugButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          setDebugEnabled(!isDebugEnabled());
        }
      });
      chatButton = new TextButton(Localization.get("hud.chat"), skin, "tiny");
      chatButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          setChatEnabled(!isChatEnabled());
        }
      });
      stage.addActor(touchpad);
      stage.addActor(debugButton);
      stage.addActor(chatButton);
    }
    setChatEnabled(false);
    setDebugEnabled(false);

    stage.addActor(crosshair);
  }

  public void render() {
    stage.getRoot().removeActor(debug);
    if (isDebugEnabled()) {
      stage.getRoot().addActor(debug);
    }
    stage.getRoot().removeActor(chat);
    stage.getRoot().removeActor(chatLog);
    if (isChatEnabled()) {
      stage.addActor(chat);
      stage.setKeyboardFocus(chat);
      stage.addActor(chatLog);
    } else {
      stage.setKeyboardFocus(null);
    }
    stage.addActor(crosshair);
    stage.act();
    stage.draw();
  }

  public boolean isDebugEnabled() {
    return debugEnabled;
  }

  public boolean isChatEnabled() {
    return chatEnabled;
  }

  public void setChatEnabled(boolean chatEnabled) {
    this.chatEnabled = chatEnabled;
  }

  public void setDebugEnabled(boolean debugEnabled) {
    if (debugEnabled) {
      GLProfiler.disable();
    } else {
      GLProfiler.enable();
    }
    this.debugEnabled = debugEnabled;
  }

  public void resize() {
    stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    debug.setBounds(0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
    debug.setAlignment(Align.topLeft, Align.topLeft);
    chat.setBounds(0, 0, Gdx.graphics.getWidth(), chat.getStyle().font.getBounds("ABC123").height * 1.5f);
    chatLog.setBounds(0, chat.getHeight(), Gdx.graphics.getWidth(), chatLog.getStyle().font.getBounds("ABC123").height * 5);

    float crosshairSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 20;
    crosshair.setBounds((Gdx.graphics.getWidth() / 2) - (crosshairSize / 2), (Gdx.graphics.getHeight() / 2) - (crosshairSize / 2), crosshairSize, crosshairSize);
    if (touchpad != null) {
      touchpad.setBounds(Gdx.graphics.getWidth() / 3 * 2, 0, Gdx.graphics.getWidth() / 3, Gdx.graphics.getWidth() / 3);
    }
    if (chatButton != null && debugButton != null) {
      float width = Math.max(chatButton.getPrefWidth(), debugButton.getPrefWidth());
      float height = Math.max(chatButton.getPrefHeight(), debugButton.getPrefHeight());
      chatButton.setBounds(Gdx.graphics.getWidth() - width, Gdx.graphics.getHeight() - height, width, height);
      debugButton.setBounds(Gdx.graphics.getWidth() - width - width, Gdx.graphics.getHeight() - height, width, height);
    }
  }

  public void print(String string) {
    chatStrings.add(0, string);
    String str = "";
    for (int i = Math.min(4, chatStrings.size() - 1); i >= 0; i--) {
      str = str + chatStrings.get(i) + "\n";
    }
    chatLog.setText(str);
  }

  @Override
  public void dispose() {
    stage.dispose();
  }

  public boolean noCursorCatching() {
    return chatEnabled;
  }
}

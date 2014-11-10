package ethanjones.cubes.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.input.keyboard.KeyTypedListener;
import ethanjones.cubes.input.keyboard.KeyboardHelper;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.side.client.ClientDebug;
import ethanjones.cubes.side.client.CubesClient;

import static ethanjones.cubes.graphics.menu.Menu.skin;

public class HudRenderer implements Disposable {

  private class KeyListener implements KeyTypedListener {

    final int debug = Input.Keys.F1;
    final int chat = Input.Keys.F2;

    boolean debugDown = false;
    boolean chatDown = false;

    @Override
    public void keyDown(int keycode) {
      if (keycode == debug && !debugDown) {
        debugDown = true;
        setDebugEnabled(!isDebugEnabled());
      }
      if (keycode == chat && !chatDown) {
        chatDown = true;
        setChatEnabled(!isChatEnabled());
      }
    }

    @Override
    public void keyUp(int keycode) {
      if (keycode == debug) debugDown = false;
      if (keycode == chat) chatDown = false;
    }

    @Override
    public void keyTyped(char character) {

    }
  }

  Stage stage;
  TextField chat;
  Image crosshair;
  Touchpad touchpad;
  TextButton debugButton;
  TextButton chatButton;
  ClientDebug.DebugLabel debug;
  private boolean chatEnabled; //TODO: On screen buttons
  private boolean debugEnabled;

  public HudRenderer() {
    stage = new Stage(new ScreenViewport());
    CubesClient.instance.inputChain.hud = stage;

    crosshair = new Image(Assets.getTextureRegion("core:hud/Crosshair.png"));

    debug = new ClientDebug.DebugLabel();

    TextField.TextFieldStyle defaultStyle = skin.get("default", TextField.TextFieldStyle.class);
    TextField.TextFieldStyle chatStyle = new TextField.TextFieldStyle(defaultStyle);
    chatStyle.background = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/ChatBackground.png"));

    chat = new TextField("", chatStyle);
    chat.setTextFieldListener(new TextField.TextFieldListener() {
      @Override
      public void keyTyped(TextField textField, char c) {
        if (c == '\n' || c == '\r') {
          PacketChat packetChat = new PacketChat();
          packetChat.msg = chat.getText();
          NetworkingManager.clientNetworking.sendToServer(packetChat);
          chat.setText("");
          setChatEnabled(false);
        }
      }
    });
    KeyboardHelper.addKeyTypedListener(new KeyListener());

    if (Compatibility.get().isTouchScreen()) {
      touchpad = new Touchpad(10f, skin);

      CubesClient.instance.inputChain.cameraController.touchpad = touchpad;

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
    if (isChatEnabled()) {
      stage.addActor(chat);
      stage.setKeyboardFocus(chat);
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

  @Override
  public void dispose() {
    stage.dispose();
  }
}

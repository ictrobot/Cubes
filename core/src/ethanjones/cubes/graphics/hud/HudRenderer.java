package ethanjones.cubes.graphics.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import java.util.ArrayList;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.gui.Fonts;
import ethanjones.cubes.graphics.gui.Gui;
import ethanjones.cubes.input.keyboard.KeyTypedAdapter;
import ethanjones.cubes.input.keyboard.KeyboardHelper;
import ethanjones.cubes.networking.NetworkingManager;
import ethanjones.cubes.networking.packets.PacketChat;
import ethanjones.cubes.side.client.ClientDebug;
import ethanjones.cubes.side.common.Cubes;

import static ethanjones.cubes.graphics.gui.Gui.batch;

public class HudRenderer implements Disposable {

  private class KeyListener extends KeyTypedAdapter {

    final int debug = Keys.F1;
    final int chat = Keys.F2;
    final int blocksMenu = Keys.E;

    @Override
    public void keyDown(int keycode) {
      if (keycode == debug) setDebugEnabled(!isDebugEnabled());
      if (keycode == chat) setChatEnabled(!isChatEnabled());
      if (keycode == blocksMenu) setBlocksMenuEnabled(!isBlocksMenuEnabled());

      if (keycode == Keys.NUM_1) Cubes.getClient().player.setSelectedSlot(0);
      if (keycode == Keys.NUM_2) Cubes.getClient().player.setSelectedSlot(1);
      if (keycode == Keys.NUM_3) Cubes.getClient().player.setSelectedSlot(2);
      if (keycode == Keys.NUM_4) Cubes.getClient().player.setSelectedSlot(3);
      if (keycode == Keys.NUM_5) Cubes.getClient().player.setSelectedSlot(4);
      if (keycode == Keys.NUM_6) Cubes.getClient().player.setSelectedSlot(5);
      if (keycode == Keys.NUM_7) Cubes.getClient().player.setSelectedSlot(6);
      if (keycode == Keys.NUM_8) Cubes.getClient().player.setSelectedSlot(7);
      if (keycode == Keys.NUM_9) Cubes.getClient().player.setSelectedSlot(8);
      if (keycode == Keys.NUM_0) Cubes.getClient().player.setSelectedSlot(9);
    }
  }

  Stage stage;

  TextField chat;
  Label chatLog;
  ArrayList<String> chatStrings = new ArrayList<String>();
  Touchpad touchpad;
  TextButton debugButton;
  TextButton chatButton;
  TextButton blockSelectorButton;
  ClientDebug.DebugLabel debug;
  KeyListener keyListener;

  Texture crosshair;
  Hotbar hotbar;
  BlockSelector blockSelector;

  private boolean chatEnabled;
  private boolean debugEnabled;
  private boolean blocksMenuEnabled;

  public HudRenderer() {
    stage = new Stage(new ScreenViewport(), batch);
    Cubes.getClient().inputChain.hud = stage;

    keyListener = new KeyListener();
    KeyboardHelper.addKeyTypedListener(keyListener);
    debug = new ClientDebug.DebugLabel();

    TextField.TextFieldStyle defaultStyle = Gui.skin.get("default", TextField.TextFieldStyle.class);
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
      touchpad = new Touchpad(10f, Gui.skin);

      Cubes.getClient().inputChain.cameraController.touchpad = touchpad;

      debugButton = new TextButton(Localization.get("hud.debug"), Gui.skin, "tiny");
      debugButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          setDebugEnabled(!isDebugEnabled());
        }
      });
      chatButton = new TextButton(Localization.get("hud.chat"), Gui.skin, "tiny");
      chatButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          setChatEnabled(!isChatEnabled());
        }
      });
      blockSelectorButton = new TextButton(Localization.get("hud.block"), Gui.skin, "tiny");
      blockSelectorButton.addListener(new ChangeListener() {
        @Override
        public void changed(ChangeEvent event, Actor actor) {
          setBlocksMenuEnabled(!isBlocksMenuEnabled());
        }
      });
      stage.addActor(touchpad);
      stage.addActor(debugButton);
      stage.addActor(chatButton);
      stage.addActor(blockSelectorButton);
    }
    setChatEnabled(false);
    setDebugEnabled(false);
    setBlocksMenuEnabled(false);

    crosshair = Assets.getTexture("core:hud/Crosshair.png");
    hotbar = new Hotbar();
    blockSelector = new BlockSelector();

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
    stage.act();
    stage.draw();

    batch.begin();
    float crosshairSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 40;
    batch.draw(crosshair, (Gdx.graphics.getWidth() / 2) - crosshairSize, (Gdx.graphics.getHeight() / 2) - crosshairSize, crosshairSize * 2, crosshairSize * 2);
    hotbar.render(batch);
    if (isBlocksMenuEnabled()) blockSelector.render(batch);
    batch.end();
  }

  public boolean isDebugEnabled() {
    return debugEnabled;
  }

  public boolean isChatEnabled() {
    return chatEnabled;
  }

  public boolean isBlocksMenuEnabled() {
    return blocksMenuEnabled;
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

  public void setBlocksMenuEnabled(boolean blocksMenuEnabled) {
    this.blocksMenuEnabled = blocksMenuEnabled;
  }

  public void resize() {
    stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    debug.setBounds(0, 0, Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight());
    debug.setAlignment(Align.topLeft, Align.topLeft);
    chat.setBounds(0, 0, Gdx.graphics.getWidth(), chat.getStyle().font.getBounds("ABC123").height * 1.5f);
    chatLog.setBounds(0, chat.getHeight(), Gdx.graphics.getWidth(), chatLog.getStyle().font.getBounds("ABC123").height * 5);

    if (touchpad != null) {
      touchpad.setBounds(Gdx.graphics.getWidth() / 3 * 2, 0, Gdx.graphics.getWidth() / 3, Gdx.graphics.getWidth() / 3);
    }
    if (chatButton != null && debugButton != null && blockSelectorButton != null) {
      float width = Math.max(chatButton.getPrefWidth(), debugButton.getPrefWidth());
      float height = Math.max(chatButton.getPrefHeight(), debugButton.getPrefHeight());
      chatButton.setBounds(Gdx.graphics.getWidth() - width, Gdx.graphics.getHeight() - height, width, height);
      debugButton.setBounds(Gdx.graphics.getWidth() - width - width, Gdx.graphics.getHeight() - height, width, height);
      blockSelectorButton.setBounds(Gdx.graphics.getWidth() - width - width - width, Gdx.graphics.getHeight() - height, width, height);
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
    return chatEnabled || blocksMenuEnabled;
  }
}

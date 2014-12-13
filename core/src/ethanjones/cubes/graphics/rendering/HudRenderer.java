package ethanjones.cubes.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.entity.living.player.Player;
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
  ClientDebug.DebugLabel debug;
  KeyListener keyListener;

  Texture crosshair;
  Texture hotbarSlot;
  Texture hotbarSelected;

  private boolean chatEnabled;
  private boolean debugEnabled;

  SpriteBatch spriteBatch;

  public HudRenderer() {
    stage = new Stage(new ScreenViewport());
    Cubes.getClient().inputChain.hud = stage;

    keyListener = new KeyListener();
    KeyboardHelper.addKeyTypedListener(keyListener);
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

    spriteBatch = new SpriteBatch();
    crosshair = Assets.getTexture("core:hud/Crosshair.png");
    hotbarSelected = Assets.getTexture("core:hud/HotbarSelected.png");
    hotbarSlot = Assets.getTexture("core:hud/HotbarSlot.png");
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

    spriteBatch.begin();
    float crosshairSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 40;
    spriteBatch.draw(crosshair, (Gdx.graphics.getWidth() / 2) - crosshairSize, (Gdx.graphics.getHeight() / 2) - crosshairSize, crosshairSize * 2, crosshairSize * 2);
    renderHotbar(spriteBatch);
    spriteBatch.end();
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

  public void renderHotbar(SpriteBatch spriteBatch) {
    Player player = Cubes.getClient().player;
    int startWidth = (Gdx.graphics.getWidth() / 2) - (hotbarSlot.getWidth() * 5);
    for (int i = 0; i < 10; i++) {
      int minX = startWidth + (i * hotbarSlot.getWidth());
      if (i == player.getSelectedSlot()) {
        spriteBatch.draw(hotbarSelected, minX, 0);
      } else {
        spriteBatch.draw(hotbarSlot, minX, 0);
      }
      Block block = player.getHotbar(i);
      if (block != null) {
        TextureRegion side = block.getTextureHandler(null).getSide(BlockFace.posX);
        spriteBatch.draw(side, minX + 8, 8);
      }
    }
  }
}

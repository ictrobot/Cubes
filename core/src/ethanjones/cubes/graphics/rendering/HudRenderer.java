package ethanjones.cubes.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

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

    setChatEnabled(false);
    setDebugEnabled(false);
  }

  public void render() {
    stage.clear();
    if (isDebugEnabled()) stage.addActor(debug);
    if (isChatEnabled()) {
      stage.addActor(chat);
      stage.setKeyboardFocus(chat);
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
    debug.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    debug.setAlignment(Align.topLeft, Align.topLeft);
    chat.setBounds(0, 0, Gdx.graphics.getWidth(), chat.getStyle().font.getBounds("ABC123").height * 1.5f);
    float crosshairSize = Math.min(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()) / 20;
    crosshair.setBounds((Gdx.graphics.getWidth() / 2) - (crosshairSize / 2), (Gdx.graphics.getHeight() / 2) - (crosshairSize / 2), crosshairSize, crosshairSize);
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}

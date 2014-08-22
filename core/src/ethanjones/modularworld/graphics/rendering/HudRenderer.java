package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.menu.actor.ResizableTextField;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.packets.PacketChat;
import ethanjones.modularworld.side.client.ClientDebug;
import ethanjones.modularworld.side.client.ModularWorldClient;

import static ethanjones.modularworld.graphics.menu.Menu.skin;

public class HudRenderer implements Disposable {

  boolean chatEnabled = false; //TODO: Keyboard shortcuts and on screen buttons
  boolean debugEnabled = false;

  Stage stage;
  ResizableTextField chat;
  ClientDebug.DebugLabel debug;

  public HudRenderer() {
    stage = new Stage(new ScreenViewport());
    ModularWorldClient.instance.inputChain.hud = stage;

    debug = new ClientDebug.DebugLabel();

    TextField.TextFieldStyle defaultStyle = skin.get("default", TextField.TextFieldStyle.class);
    TextField.TextFieldStyle chatStyle = new TextField.TextFieldStyle(defaultStyle);
    chatStyle.background = new TextureRegionDrawable(GraphicsHelper.getTexture("hud/ChatBackground.png").textureRegion);

    chat = new ResizableTextField("", chatStyle);
    chat.setTextFieldListener(new TextField.TextFieldListener() {
      @Override
      public void keyTyped(TextField textField, char c) {
        if (c == '\n' || c == '\r') {
          PacketChat packetChat = new PacketChat();
          packetChat.msg = chat.getText();
          NetworkingManager.clientNetworking.sendToServer(packetChat);
          chat.setText("");
          chatEnabled = false;
        }
      }
    });
  }

  public void render() {
    stage.clear();
    if (debugEnabled) stage.addActor(debug);
    if (chatEnabled) {
      stage.addActor(chat);
      stage.setKeyboardFocus(chat);
    }
    stage.act();
    stage.draw();
  }

  public void resize() {
    stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    chat.setBounds(0, 0, Gdx.graphics.getWidth(), chat.getStyle().font.getBounds("ABC123").height * 1.5f);
  }

  @Override
  public void dispose() {
    stage.dispose();
  }
}

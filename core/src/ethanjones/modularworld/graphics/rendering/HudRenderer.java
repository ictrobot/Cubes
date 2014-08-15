package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.networking.NetworkingManager;
import ethanjones.modularworld.networking.packets.PacketChat;
import ethanjones.modularworld.side.client.ClientDebug;
import ethanjones.modularworld.side.client.ModularWorldClient;

import static ethanjones.modularworld.graphics.menu.Menu.skin;

public class HudRenderer implements Disposable {

  Stage hud;
  TextField chat;

  public HudRenderer() {
    hud = new Stage(new ScreenViewport());
    ModularWorldClient.instance.inputChain.hud = hud;

    for (ClientDebug.DebugLabel f : ClientDebug.getLabels(skin)) {
      hud.addActor(f);
    }

    TextField.TextFieldStyle defaultStyle = skin.get("default", TextField.TextFieldStyle.class);
    TextField.TextFieldStyle chatStyle = new TextField.TextFieldStyle(defaultStyle);
    chatStyle.background = new TextureRegionDrawable(GraphicsHelper.getTexture("hud/ChatBackground.png").textureRegion);

    hud.addActor(chat = new TextField("", chatStyle));
    hud.setKeyboardFocus(chat);
    chat.setTextFieldListener(new TextField.TextFieldListener() {
      @Override
      public void keyTyped(TextField textField, char c) {
        if (c == '\n' || c == '\r') {
          PacketChat packetChat = new PacketChat();
          packetChat.msg = chat.getText();
          NetworkingManager.clientNetworking.sendToServer(packetChat);
          chat.setText("");
        }
      }
    });
  }

  public void render() {
    hud.act();
    hud.draw();
  }

  public void resize() {
    hud.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    ClientDebug.DebugLabel.resizeAll();
    chat.setBounds(0, 0, Gdx.graphics.getWidth(), chat.getPrefHeight());
  }

  @Override
  public void dispose() {
    hud.dispose();
  }
}

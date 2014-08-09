package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ethanjones.modularworld.core.hud.Chat;
import ethanjones.modularworld.core.hud.ChatManager;
import ethanjones.modularworld.side.client.ModularWorldClient;
import ethanjones.modularworld.side.client.debug.Debug;
import ethanjones.modularworld.side.client.debug.DebugLabel;

/**
 * https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/
 * UISimpleTest.java
 */
public class HudRenderer implements Disposable {

  Skin skin;
  Stage hud;
  Chat chat;

  public HudRenderer() {
    hud = new Stage(new ScreenViewport());
    ModularWorldClient.instance.inputChain.hud = hud;

    skin = new Skin();
    skin.add("default", new BitmapFont());
    skin.add("default", new LabelStyle(skin.getFont("default"), Color.WHITE));

    for (DebugLabel f : Debug.getLabels(skin)) {
      hud.addActor(f);
    }

    hud.addActor(chat = ChatManager.getChat(skin));
  }

  public void render() {
    hud.act();
    hud.draw();
  }

  public void resize() {
    hud.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    DebugLabel.resizeAll();
    chat.resize();
  }

  @Override
  public void dispose() {
    hud.dispose();
  }
}

package ethanjones.modularworld.graphics.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.core.debug.Debug;
import ethanjones.modularworld.core.debug.DebugLabel;

/**
 * https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/
 * UISimpleTest.java
 */
public class HudRenderer {

  Skin skin;
  Stage hud;

  public HudRenderer() {
    hud = new Stage(new ScreenViewport());
    ModularWorld.instance.inputChain.hud = hud;

    skin = new Skin();
    skin.add("default", new BitmapFont());
    skin.add("default", new LabelStyle(skin.getFont("default"), Color.WHITE));

    // Table table = new Table();
    // table.setFillParent(true);
    // hud.addActor(table);

    for (DebugLabel f : Debug.getLabels(skin)) {
      hud.addActor(f);
    }
  }

  public void render() {
    hud.act();
    hud.draw();
  }

  public void resize() {
    hud.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    DebugLabel.resizeAll();
  }

}

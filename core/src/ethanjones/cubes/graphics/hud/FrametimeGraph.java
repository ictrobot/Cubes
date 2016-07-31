package ethanjones.cubes.graphics.hud;

import ethanjones.cubes.core.settings.Settings;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.LinkedList;

public class FrametimeGraph {
  private static LinkedList<Float> frametimes = new LinkedList<Float>();
  private static Texture textureBar = Assets.getTexture("core:hud/FrametimeGraphBar.png");
  private static Texture textureLine = Assets.getTexture("core:hud/FrametimeGraphLine.png");
  private static Texture textureLine60 = Assets.getTexture("core:hud/FrametimeGraphLine60.png");
  private static final int scale = 10;
  private static final float yOffset = Fonts.scaleFactor * 50f;

  public static void draw(Batch batch) {
    if (!Settings.getBooleanSettingValue(Settings.DEBUG_FRAMETIME_GRAPH)) return;

    while (frametimes.size() < Gdx.graphics.getWidth()) {
      frametimes.addFirst(0f);
    }
    for (int i = 0; i < frametimes.size(); i++) {
      batch.draw(textureBar, i, yOffset, 1f, frametimes.get(i) * scale);
    }
    for (int i = 0; i <= 16; i++) {
      batch.draw(textureLine, 0, yOffset + (scale * i), Gdx.graphics.getWidth(), 1);
    }
    batch.draw(textureLine60, 0, yOffset + (scale * 16.666666f), Gdx.graphics.getWidth(), 1);
  }

  public static void update() {
    frametimes.addLast(Gdx.graphics.getRawDeltaTime() * 1000f);
    while (frametimes.size() > Gdx.graphics.getWidth()) {
      frametimes.removeFirst();
    }
  }
}

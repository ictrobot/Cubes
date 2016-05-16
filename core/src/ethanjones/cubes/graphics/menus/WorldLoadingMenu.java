package ethanjones.cubes.graphics.menus;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class WorldLoadingMenu extends InfoMenu {

  public static final ProgressBar.ProgressBarStyle PROGRESS_BAR_STYLE = new ProgressBar.ProgressBarStyle();

  static {
    PROGRESS_BAR_STYLE.knobBefore = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/ProgressBarBefore.png")) {
      @Override
      public float getMinHeight() {
        return Fonts.scaleFactor * 15f;
      }
    };
    PROGRESS_BAR_STYLE.knobAfter = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/ProgressBarAfter.png")) {
      @Override
      public float getMinHeight() {
        return Fonts.scaleFactor * 15f;
      }
    };
  }

  private ProgressBar progressBar;

  public WorldLoadingMenu() {
    super(Localization.get("menu.general.loading"), false);

    progressBar = new ProgressBar(0f, 1f, 0.001f, false, PROGRESS_BAR_STYLE);
    stage.addActor(progressBar);
  }

  @Override
  public void resize(int width, int height) {
    super.resize(width, height);
    float x = (Gdx.graphics.getWidth() / 2) - (text.getPrefWidth() * 1.5f);
    float y = (Gdx.graphics.getHeight() / 2) - (text.getPrefHeight() * 1.5f); // drawable wont strech
    progressBar.setBounds(x, y, text.getPrefWidth() * 3f, Fonts.scaleFactor * 15f);
  }

  @Override
  public void render() {
    if (Cubes.getClient() != null) progressBar.setValue(Cubes.getClient().worldProgress);
    super.render();
  }
}

package ethanjones.cubes.graphics.menus;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.common.Cubes;

public class WorldLoadingMenu extends InfoMenu {

  public static final ProgressBar.ProgressBarStyle PROGRESS_BAR_STYLE = new ProgressBar.ProgressBarStyle();

  static {
    PROGRESS_BAR_STYLE.knobBefore = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/ProgressBarBefore.png")) {
      @Override
      public float getMinHeight() {
        return 15f;
      }
    };
    PROGRESS_BAR_STYLE.knobAfter = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/ProgressBarAfter.png")) {
      @Override
      public float getMinHeight() {
        return 15f;
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
  public void resize(float width, float height) {
    super.resize(width, height);
    float x = (width / 2) - (text.getPrefWidth() * 1.5f);
    float y = (height / 2) - (text.getPrefHeight() * 1.5f); // drawable wont stretch
    progressBar.setBounds(x, y, text.getPrefWidth() * 3f, 15f);
  }

  @Override
  public void render() {
    if (Cubes.getClient() != null) progressBar.setValue(Cubes.getClient().worldProgress);
    super.render();
  }

  @Override
  public boolean blockClientInput() {
    return true;
  }
}

package ethanjones.cubes.graphics.hud;

import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class ImageButtons {

  public static ImageButton.ImageButtonStyle jumpButton() {
    TextureRegionDrawable t = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/JumpButton.png"));
    return new ImageButton.ImageButtonStyle(t, t, null, null, null, null);
  }

  public static ImageButton.ImageButtonStyle blocksButton() {
    TextureRegionDrawable up = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/BlocksButtonUp.png"));
    TextureRegionDrawable down = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/BlocksButtonDown.png"));
    return new ImageButton.ImageButtonStyle(up, down, null, null, null, null);
  }

  public static ImageButton.ImageButtonStyle chatButton() {
    TextureRegionDrawable up = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/ChatButtonUp.png"));
    TextureRegionDrawable down = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/ChatButtonDown.png"));
    return new ImageButton.ImageButtonStyle(up, down, null, null, null, null);
  }

  public static ImageButton.ImageButtonStyle debugButton() {
    TextureRegionDrawable up = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/DebugButtonUp.png"));
    TextureRegionDrawable down = new TextureRegionDrawable(Assets.getTextureRegion("core:hud/touch/DebugButtonDown.png"));
    return new ImageButton.ImageButtonStyle(up, down, null, null, null, null);
  }
}

package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.entity.living.player.PlayerInventory;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

public class HotbarActor extends Image {
  public static final TextureRegion hotbar = Assets.getTextureRegion("core:hud/HotbarSlot.png");
  public static final TextureRegion hotbarSelected = Assets.getTextureRegion("core:hud/HotbarSelected.png");
  public static final InputListener scroll = new InputListener() {
    @Override
    public boolean scrolled(InputEvent event, float x, float y, int amount) {
      PlayerInventory player = Cubes.getClient().player.getInventory();
      if (amount > 1) amount = 1;
      if (amount < -1) amount = -1;
      player.hotbarSelected += amount;
      if (player.hotbarSelected == -1) player.hotbarSelected = 8;
      if (player.hotbarSelected == 9) player.hotbarSelected = 0;
      return true;
    }
  };

  private PlayerInventory playerInventory;

  public HotbarActor(PlayerInventory inventory) {
    this.playerInventory = inventory;
    setDrawable(new HotbarDrawable());
    pack();
  }

  private class HotbarDrawable extends BaseDrawable {

    private HotbarDrawable() {
      setMinWidth(432f);
      setMinHeight(48f);
    }

    public void draw(Batch batch, float x, float y, float width, float height) {

      for (int i = 0; i < 9; i++) {
        if (i == playerInventory.hotbarSelected) {
          batch.draw(hotbarSelected, x + (48f * i), y, 48f, 48f);
        } else {
          batch.draw(hotbar, x + (48f * i), y, 48f, 48f);
        }
        if (playerInventory.itemStacks[i] != null) {
          TextureRegion region = playerInventory.itemStacks[i].item.getTextureRegion();
          batch.draw(region, x + (48f * i) + 8f, y + 8f, 32f, 32f);
        }
      }
    }
  }

}

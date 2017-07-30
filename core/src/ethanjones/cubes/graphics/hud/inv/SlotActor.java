package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.Inventory;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class SlotActor extends Button {
  public static final TextureRegion blank = Assets.getTextureRegion("core:hud/inv/Slot.png");

  private final Inventory inventory;
  private final int num;

  public SlotActor(Inventory inventory, int num) {
    super(new ButtonStyle());

    Image image = new Image();
    image.setScaling(Scaling.fit);
    image.setDrawable(new SlotDrawable());
    image.setTouchable(Touchable.disabled);
    add(image);
    setSize(getPrefWidth(), getPrefHeight());

    this.inventory = inventory;
    this.num = num;

    InventoryManager.newSlot(this);
    addListener(new SlotTooltipListener(this));
  }

  public Inventory getInventory() {
    return inventory;
  }

  public int getNum() {
    return num;
  }

  public ItemStack getStack() {
    return inventory.itemStacks[num];
  }

  private class SlotDrawable extends BaseDrawable {

    private SlotDrawable() {
      setMinWidth(36f);
      setMinHeight(36f);
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
      batch.draw(blank, x, y, width, height);
      if (inventory.itemStacks[num] != null) {
        TextureRegion region = inventory.itemStacks[num].getTextureRegion();
        batch.draw(region, x + 2f, y + 2f, width - 4f, height - 4f);
        drawText(batch, x + 2f, y + 2f, inventory.itemStacks[num]);
      }
    }
  }

  public static void drawText(Batch batch, float x, float y, ItemStack itemStack) {
    if (itemStack == null || itemStack.item.getStackCountMax() == 1) return;
    BitmapFontCache cache = Fonts.smallHUD.getCache();
    cache.clear();
    GlyphLayout layout = cache.addText(Integer.toString(itemStack.count), x, y, 32f, Align.right, false);
    cache.translate(0, layout.height);
    cache.draw(batch);
  }

}

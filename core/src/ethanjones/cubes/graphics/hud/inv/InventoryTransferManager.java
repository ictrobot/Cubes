package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.Inventory;
import ethanjones.cubes.item.inv.InventoryHelper;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

import static ethanjones.cubes.graphics.hud.inv.InventoryManager.GROUP_INVENTORY;

public class InventoryTransferManager {

  protected static ItemActor itemActor;
  private static Modifier m = Compatibility.get().isTouchScreen() ? new TouchModifier() : new MouseModifier();

  private static Actor selected;

  static {
    if (Compatibility.get().isTouchScreen()) selected = new Image(new ItemDrawable(true));
  }

  public static void resize() {
    if (selected != null) selected.setPosition(0f, 0f);
  }

  public static void reset() {
    itemActor = null;
    if (selected != null) InventoryManager.GROUP_SHOWN.addActor(selected);
  }

  protected static void newSlot(final SlotActor slot) {
    final Inventory inventory = slot.getInventory();
    final int num = slot.getNum();

    slot.addListener(new InputListener() {

      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (itemActor == null) {
          ItemStack stack = inventory.itemStacks[num];
          if (stack == null) return true;

          ItemStack payloadStack = stack;
          if (!inventory.fixed) {
            if (m.modifier() && !inventory.output) {
              int n = stack.count / 2;
              payloadStack = stack.copy();
              payloadStack.count = n;
              stack.count -= n;
            } else {
              inventory.itemStacks[num] = null;
            }
            inventory.sync();
          } else {
            payloadStack = payloadStack.copy();
          }

          ItemActor itemActor = new ItemActor(payloadStack, inventory, num);
          itemActor.center(event.getStageX(), event.getStageY());
          setup(slot, itemActor);
        } else {
          doTransfer(slot);
        }
        return true;
      }
    });
  }

  private static void setup(SlotActor slot, ItemActor newActor) {
    if (itemActor != null) GROUP_INVENTORY.removeActor(itemActor);
    itemActor = newActor;
    GROUP_INVENTORY.addActor(newActor);
  }

  private static boolean doTransfer(SlotActor slotActor) {
    Inventory targetInv = slotActor.getInventory();
    int targetNum = slotActor.getNum();
    if (targetInv.output && InventoryHelper.sameItem(itemActor.itemStack, targetInv.itemStacks[targetNum])) {
      if (itemActor.itemStack.count + targetInv.itemStacks[targetNum].count < itemActor.itemStack.item.getStackCountMax()) {
        itemActor.itemStack.count += targetInv.itemStacks[targetNum].count;
        targetInv.itemStacks[targetNum] = null;
        targetInv.sync();
        return true;
      }
    }
    if (targetInv.cancelInputItems) {
      return true;
    } else if (targetInv.fixed) {
      if (targetInv.voidInputItems) {
        itemActor.itemStack.count = 0;
      } else {
        return true;
      }
    } else if (targetInv.itemStacks[targetNum] == null) {
      ItemStack copy = itemActor.itemStack.copy();
      if (m.modifier()) {
        copy.count = 1;
        targetInv.itemStacks[targetNum] = copy;
        itemActor.itemStack.count--;
      } else {
        targetInv.itemStacks[targetNum] = copy;
        itemActor.itemStack.count = 0;
      }
    } else if (InventoryHelper.sameItem(targetInv.itemStacks[targetNum], itemActor.itemStack)) {
      int transferAmount = Math.min(m.modifier() ? 1 : itemActor.itemStack.count, itemActor.itemStack.item.getStackCountMax() - targetInv.itemStacks[targetNum].count);
      targetInv.itemStacks[targetNum].count += transferAmount;
      itemActor.itemStack.count -= transferAmount;
    } else {
      ItemStack i = itemActor.itemStack;
      itemActor.itemStack = targetInv.itemStacks[targetNum];
      targetInv.itemStacks[targetNum] = i;
    }
    if (itemActor.itemStack.count == 0) {
      itemActor.itemStack = null;
      itemActor.remove();
      itemActor = null;
    }
    targetInv.sync();
    return true;
  }

  protected static class ItemActor extends Image {

    private ItemStack itemStack;
    private final Inventory inventory;
    private final int num;

    public ItemActor(final ItemStack itemStack, Inventory inventory, final int num) {
      super(new ItemDrawable(false));
      this.itemStack = itemStack;
      this.inventory = inventory;
      this.num = num;
      setSize(32f, 32f);
      setTouchable(Touchable.disabled);
      setVisible(!Compatibility.get().isTouchScreen());
    }

    @Override
    public void act(float delta) {
      toFront();
      super.act(delta);
    }

    @Override
    protected void setStage(Stage stage) {
      if (stage == null && itemStack != null && !inventory.fixed) {
        if (inventory.itemStacks[num] == null) {
          inventory.itemStacks[num] = itemStack;
        } else if (InventoryHelper.sameItem(inventory.itemStacks[num], itemStack)) {
          inventory.itemStacks[num].count += itemStack.count;
        }
        inventory.sync();
        itemActor = null;
      }
      super.setStage(stage);
    }

    public void center(float stageX, float stageY) {
      setPosition(stageX - (getWidth() / 2f), stageY - (getHeight() / 2f));
    }
  }


  private static class ItemDrawable extends BaseDrawable {
    public static final TextureRegion b = Assets.getTextureRegion("core:hud/touch/InventoryTransfer.png");

    private final boolean background;

    private ItemDrawable(boolean background) {
      this.background = background;
      setMinWidth(background ? 68f : 32f);
      setMinHeight(background ? 68f : 32f);
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
      if (itemActor == null) return;
      ItemStack itemStack = itemActor.itemStack;
      if (itemStack == null) return;
      if (background) {
        batch.draw(b, x, y, width, height);
        TextureRegion region = itemStack.item.getTextureRegion();
        batch.draw(region, x + 18f, y + 18f, 32f, 32f);
        SlotActor.drawText(batch, x + 18f, y + 18f, itemStack);
      } else {
        TextureRegion region = itemStack.item.getTextureRegion();
        batch.draw(region, x, y, width, height);
        SlotActor.drawText(batch, x, y, itemStack);
      }
    }
  }

  private interface Modifier {

    public boolean modifier();

  }

  private static class MouseModifier implements Modifier {

    @Override
    public boolean modifier() {
      return Gdx.input.isButtonPressed(Buttons.RIGHT);
    }

  }

  private static class TouchModifier implements Modifier {

    @Override
    public boolean modifier() {
      return Cubes.getClient().renderer.guiRenderer.inventoryModifierButton.isPressed();
    }
  }
}

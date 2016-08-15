package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.Inventory;
import ethanjones.cubes.item.inv.InventoryHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;

import static ethanjones.cubes.graphics.hud.inv.InventoryManager.GROUP_INVENTORY;

public class InventoryManagerDesktop {

  protected static ItemActor itemActor;

  protected static void newSlot(final SlotActor slot) {
    final Inventory inventory = slot.getInventory();
    final int num = slot.getNum();

    slot.addListener(new InputListener() {

      @Override
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        ItemStack stack = inventory.itemStacks[num];
        if (stack == null) return true;

        ItemStack payloadStack = stack;
        if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
          int n = stack.count / 2;
          payloadStack = stack.copy();
          payloadStack.count = n;
          stack.count -= n;
        } else {
          inventory.itemStacks[num] = null;
        }
        inventory.sync();

        ItemActor itemActor = new ItemActor(payloadStack, inventory, num);
        itemActor.center(event.getStageX(), event.getStageY());
        setup(slot, itemActor);
        return true;
      }
    });
  }

  private static void setup(SlotActor slot, ItemActor newActor) {
    if (itemActor != null) GROUP_INVENTORY.removeActor(itemActor);
    itemActor = newActor;
    GROUP_INVENTORY.addActor(newActor);
  }

  protected static class ItemActor extends Image {

    private ItemStack itemStack;
    private final Inventory inventory;
    private final int num;

    public ItemActor(final ItemStack itemStack, Inventory inventory, final int num) {
      super(new ItemDrawable(itemStack));
      this.itemStack = itemStack;
      this.inventory = inventory;
      this.num = num;
      setSize(32f, 32f);
      addTouchDownListener();
    }

    private void addTouchDownListener() {
      addListener(new InputListener() {
        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
          setTouchable(Touchable.disabled);
          Actor hit = GROUP_INVENTORY.hit(event.getStageX(), event.getStageY(), true);
          setTouchable(Touchable.enabled);
          if (hit instanceof SlotActor) {
            SlotActor slotActor = (SlotActor) hit;
            Inventory targetInv = slotActor.getInventory();
            int targetNum = slotActor.getNum();
            if (targetInv.itemStacks[targetNum] == null) {
              ItemStack copy = itemStack.copy();
              if (button == Buttons.RIGHT) {
                copy.count = 1;
                targetInv.itemStacks[targetNum] = copy;
                itemStack.count--;
              } else {
                targetInv.itemStacks[targetNum] = copy;
                itemStack.count = 0;
              }
            } else if (InventoryHelper.sameItem(targetInv.itemStacks[targetNum], itemStack)) {
              int transferAmount = Math.min(button == Buttons.RIGHT ? 1 : itemStack.count, itemStack.item.getStackCountMax() - targetInv.itemStacks[targetNum].count);
              targetInv.itemStacks[targetNum].count += transferAmount;
              itemStack.count -= transferAmount;
            } else {
              ItemStack i = itemStack;
              itemStack = targetInv.itemStacks[targetNum];
              targetInv.itemStacks[targetNum] = i;
              ((ItemDrawable) getDrawable()).setItemStack(itemStack);
            }
            if (itemStack.count == 0) {
              itemStack = null;
              remove();
            }
            targetInv.sync();
          }
          return true;
        }
      });
    }

    @Override
    protected void setStage(Stage stage) {
      if (stage == null && itemStack != null) {
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

    private ItemStack itemStack;

    private ItemDrawable(ItemStack itemStack) {
      this.itemStack = itemStack;
      setMinWidth(32f);
      setMinHeight(32f);
    }

    public void setItemStack(ItemStack itemStack) {
      this.itemStack = itemStack;
    }

    public void draw(Batch batch, float x, float y, float width, float height) {
      TextureRegion region = itemStack.item.getTextureRegion();
      batch.draw(region, x, y, width, height);
      SlotActor.drawText(batch, x, y, itemStack);
    }
  }
}

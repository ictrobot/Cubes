package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.Inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InventoryManagerTouchscreen {
  private static SlotActor selected;

  protected static void newSlot(final SlotActor slot) {
    final Inventory targetInv = slot.getInventory();
    final int targetNum = slot.getNum();

    slot.addListener(new ClickListener() {

      @Override
      public void clicked(InputEvent event, float x, float y) {
        if (selected == null) {
          if (targetInv.itemStacks[targetNum] != null && getTapCount() == 2) {
            selected = slot;
          }
        } else {
          Inventory selectedInv = selected.getInventory();
          int selectedNum = selected.getNum();

          ItemStack itemStack = targetInv.itemStacks[targetNum];
          if (targetInv.fixed) {
            if (targetInv.voidItems && !selectedInv.fixed) {
              selectedInv.itemStacks[selectedNum] = null;
            }
          } else {
            targetInv.itemStacks[targetNum] = selectedInv.itemStacks[selectedNum];
            if (!selectedInv.fixed) {
              selectedInv.itemStacks[selectedNum] = itemStack;
            }
          }

          if (selectedInv == targetInv) {
            targetInv.sync();
          } else {
            targetInv.sync();
            selectedInv.sync();
          }
          selected = null;
        }

      }
    });
  }

  protected static boolean isSelected(SlotActor slot) {
    return slot == selected;
  }
}

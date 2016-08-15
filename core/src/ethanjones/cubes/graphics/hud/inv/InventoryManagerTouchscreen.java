package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.Inventory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InventoryManagerTouchscreen {
  private static SlotActor selected;

  protected static void newSlot(final SlotActor slot) {
    final Inventory inventory = slot.getInventory();
    final int num = slot.getNum();

    slot.addListener(new ClickListener() {

      @Override
      public void clicked(InputEvent event, float x, float y) {
        if (selected == null) {
          if (inventory.itemStacks[num] != null && getTapCount() == 2) {
            selected = slot;
          }
        } else {
          Inventory selectedInv = selected.getInventory();
          int selectedNum = selected.getNum();

          ItemStack itemStack = inventory.itemStacks[num];
          inventory.itemStacks[num] = selectedInv.itemStacks[selectedNum];
          selectedInv.itemStacks[selectedNum] = itemStack;

          if (selectedInv == inventory) {
            inventory.sync();
          } else {
            inventory.sync();
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

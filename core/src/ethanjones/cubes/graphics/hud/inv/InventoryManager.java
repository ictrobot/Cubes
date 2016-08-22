package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.graphics.menu.MenuTools;

import com.badlogic.gdx.scenes.scene2d.*;

public class InventoryManager {

  public static final Group GROUP_INVENTORY = new Group();
  public static final Group GROUP_HIDDEN = new Group(); // hidden when inventory is open
  public static final Group GROUP_SHOWN = new Group(); // shown when inventory is open

  public static final InputListener moveItem = new InputListener() {
    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
      if (InventoryTransferManager.itemActor != null) {
        InventoryTransferManager.itemActor.center(event.getStageX(), event.getStageY());
        return true;
      }
      return false;
    }
  };
  private static Actor openInventory;

  public static void setup(Stage stage) {
    reset();
    stage.addActor(GROUP_INVENTORY);
    stage.addActor(GROUP_SHOWN);
    stage.addActor(GROUP_HIDDEN);
    stage.addListener(moveItem);
  }

  public static void reset() {
    openInventory = null;
    GROUP_INVENTORY.clear();
    GROUP_INVENTORY.remove();
    GROUP_SHOWN.clear();
    GROUP_SHOWN.setVisible(false);
    GROUP_HIDDEN.clear();
    GROUP_HIDDEN.setVisible(true);
    if (!Compatibility.get().isTouchScreen()) GROUP_INVENTORY.addListener(moveItem);
    InventoryTransferManager.reset();
  }

  public static void resize() {
    if (openInventory != null) MenuTools.center(openInventory);
    InventoryTransferManager.resize();
  }

  public static boolean isInventoryOpen() {
    return GROUP_INVENTORY.hasChildren();
  }

  public static void showInventory(Actor actor) {
    openInventory = actor;
    GROUP_INVENTORY.addActor(actor);
    GROUP_INVENTORY.addActor(SlotTooltipListener.tooltip);
    GROUP_INVENTORY.toFront();
    GROUP_SHOWN.setVisible(true);
    GROUP_HIDDEN.setVisible(false);
    resize();
  }

  public static void hideInventory() {
    openInventory = null;
    GROUP_INVENTORY.clear();
    GROUP_SHOWN.setVisible(false);
    GROUP_HIDDEN.setVisible(true);
  }

  public static void newSlot(final SlotActor slot) {
    InventoryTransferManager.newSlot(slot);
  }
}

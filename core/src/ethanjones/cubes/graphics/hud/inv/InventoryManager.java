package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.core.platform.Compatibility;
import ethanjones.cubes.graphics.menu.MenuTools;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;

public class InventoryManager {

  public static final Group GROUP_INVENTORY = new Group();
  public static final InputListener moveItem = new InputListener() {
    @Override
    public boolean mouseMoved(InputEvent event, float x, float y) {
      if (InventoryManagerDesktop.itemActor != null) {
        InventoryManagerDesktop.itemActor.center(event.getStageX(), event.getStageY());
        return true;
      }
      return false;
    }
  };
  private static Actor openInventory;

  public static void reset() {
    openInventory = null;
    GROUP_INVENTORY.clear();
    GROUP_INVENTORY.remove();
    if (!Compatibility.get().isTouchScreen()) GROUP_INVENTORY.addListener(moveItem);
    InventoryManagerDesktop.itemActor = null;
  }

  public static void resize() {
    if (openInventory != null) MenuTools.center(openInventory);
  }

  public static boolean isInventoryOpen() {
    return GROUP_INVENTORY.hasChildren();
  }

  public static void showInventory(Actor actor) {
    openInventory = actor;
    GROUP_INVENTORY.addActor(actor);
    GROUP_INVENTORY.toFront();
    resize();
  }

  public static void hideInventory() {
    openInventory = null;
    GROUP_INVENTORY.clear();
    Cubes.getClient().renderer.guiRenderer.playerInvToggle.disable();
  }

  public static void newSlot(final SlotActor slot) {
    if (Compatibility.get().isTouchScreen()) {
      InventoryManagerTouchscreen.newSlot(slot);
    } else {
      InventoryManagerDesktop.newSlot(slot);
    }
  }
}

package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.item.ItemStack;
import ethanjones.cubes.item.inv.Inventory;
import ethanjones.cubes.item.inv.InventoryHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

public class SlotSource extends Source {
  public static DragAndDrop dragAndDrop = new DragAndDrop();

  static {
    dragAndDrop.setDragActorPosition(-16f, 16f);
    dragAndDrop.setDragTime(100);
    dragAndDrop.setButton(-1);
  }

  private Inventory sourceInv;
  private int sourceNum;

  public SlotSource(SlotActor actor) {
    super(actor);
    this.sourceInv = actor.getInventory();
    this.sourceNum = actor.getNum();
  }

  @Override
  public Payload dragStart(InputEvent event, float x, float y, int pointer) {
    if (sourceInv.itemStacks[sourceNum] == null) return null;

    Payload payload = new Payload();
    ItemStack payloadStack = sourceInv.itemStacks[sourceNum];
    if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
      int n = payloadStack.count / 2;
      payloadStack = payloadStack.copy();
      payloadStack.count = n;
      sourceInv.itemStacks[sourceNum].count -= n;
    } else {
      sourceInv.itemStacks[sourceNum] = null;
    }
    sourceInv.sync();
    payload.setObject(payloadStack);

    Actor dragActor = new Image(payloadStack.item.getTextureRegion());
    payload.setDragActor(dragActor);

    return payload;
  }

  @Override
  public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
    ItemStack payloadStack = (ItemStack) payload.getObject();

    if (target != null) {
      Inventory targetInv = ((SlotActor) target.getActor()).getInventory();
      int targetNum = ((SlotActor) target.getActor()).getNum();
      ItemStack targetStack = targetInv.itemStacks[targetNum];

      if (targetStack == null) {
        targetInv.itemStacks[targetNum] = payloadStack;

        targetInv.sync();
      } else if (InventoryHelper.sameItem(targetStack, payloadStack)) {
        int max = targetStack.item.getStackCountMax();
        if (targetStack.count + payloadStack.count <= max) {
          targetStack.count += payloadStack.count;

          targetInv.sync();
        } else {
          int transfer = max - targetStack.count;
          targetStack.count += transfer;
          payloadStack.count -= transfer;
          if (InventoryHelper.sameItem(sourceInv.itemStacks[sourceNum], payloadStack)) {
            sourceInv.itemStacks[sourceNum].count += payloadStack.count;
          } else {
            sourceInv.itemStacks[sourceNum] = payloadStack;
          }

          targetInv.sync();
          sourceInv.sync();
        }
      } else {
        if (InventoryHelper.sameItem(sourceInv.itemStacks[sourceNum], payloadStack)) {
          payloadStack.count += sourceInv.itemStacks[sourceNum].count;
        }
        sourceInv.itemStacks[sourceNum] = targetStack;
        targetInv.itemStacks[targetNum] = payloadStack;

        sourceInv.sync();
        targetInv.sync();
      }
    } else {
      sourceInv.itemStacks[sourceNum] = payloadStack;

      sourceInv.sync();
    }
  }
}

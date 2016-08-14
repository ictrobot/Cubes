package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.item.inv.Inventory;

import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;

public class SlotTarget extends Target {

  private Inventory targetInv;
  private int targetNum;

  public SlotTarget(SlotActor actor) {
    super(actor);
    this.targetInv = actor.getInventory();
    this.targetNum = actor.getNum();
  }

  @Override
  public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
    return true;
  }

  @Override
  public void drop(Source source, Payload payload, float x, float y, int pointer) {

  }

}

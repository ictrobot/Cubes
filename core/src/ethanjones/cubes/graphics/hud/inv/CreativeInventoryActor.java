package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.item.inv.CreativeInventory;
import ethanjones.cubes.side.common.Cubes;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class CreativeInventoryActor extends ScrollInventoryActor {

  public CreativeInventoryActor() {
    super(new CreativeInventory(), 3);

    inner.add().height(8f).colspan(9);
    inner.row();
    inner.add(new CraftingInventoryActor(false)).space(0).colspan(9);
  }

  @Override
  protected void setStage(Stage stage) {
    Stage old = getStage();
    if (old != null) old.setScrollFocus(Cubes.getClient().renderer.guiRenderer.hotbar);
    super.setStage(stage);
    if (stage != null) stage.setScrollFocus(scrollPane);
  }
}

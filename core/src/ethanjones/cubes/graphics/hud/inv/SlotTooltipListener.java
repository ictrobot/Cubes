package ethanjones.cubes.graphics.hud.inv;

import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.item.ItemStack;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class SlotTooltipListener extends InputListener {

  public static SlotTooltip tooltip = new SlotTooltip();
  private static boolean inside;
  private static Vector2 position = new Vector2();
  private static Vector2 tmp = new Vector2();
  private static Vector2 offset = new Vector2(4, 4);

  private final SlotActor actor;

  public SlotTooltipListener(SlotActor actor) {
    this.actor = actor;
  }

  @Override
  public boolean mouseMoved(InputEvent event, float x, float y) {
    if (inside) {
      event.getListenerActor().localToStageCoordinates(tmp.set(x, y));
      tooltip.setPosition(tmp.x + position.x + offset.x, tmp.y + position.y + offset.y);
    }
    return false;
  }

  @Override
  public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
    ItemStack stack = actor.getStack();
    if (stack == null) return;
    inside = true;
    tooltip.setItemStack(stack);
    tooltip.setVisible(true);
    tmp.set(x, y);
    event.getListenerActor().localToStageCoordinates(tmp);
    tooltip.setPosition(tmp.x + position.x + offset.x, tmp.y + position.y + offset.y);
    tooltip.toFront();
  }

  @Override
  public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
    inside = false;
    tooltip.setVisible(false);
  }

  private static class SlotTooltip extends Window {

    private static final NinePatchDrawable slotBackground = new NinePatchDrawable(new NinePatch(Assets.getTextureRegion("core:hud/inv/Tooltip.png"), 4, 4, 4, 4));
    private static final WindowStyle style = new WindowStyle(Fonts.hud, Color.WHITE, slotBackground);
    private Label label = new Label("", new LabelStyle(Fonts.hud, Color.WHITE));

    public SlotTooltip() {
      super("", style);
      setVisible(false);
      add(label);
    }

    public void setItemStack(ItemStack itemStack) {
      label.setText(itemStack.item.getName());
      pack();
    }
  }
}

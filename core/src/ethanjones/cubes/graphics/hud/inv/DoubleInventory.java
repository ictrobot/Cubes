package ethanjones.cubes.graphics.hud.inv;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

public class DoubleInventory extends VerticalGroup {

  public DoubleInventory(Actor a, Actor b) {
    addActor(a);
    addActor(b);
  }

}

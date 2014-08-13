package ethanjones.modularworld.side.client.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.ArrayList;

public class DebugLabel extends Label {

  private static ArrayList<DebugLabel> labels = new ArrayList<DebugLabel>();
  private static int LINE_SPACING = 15;

  private DebugType debugType;

  public DebugLabel(DebugType debugType, Skin skin) {
    super(debugType.name(), skin);
    labels.add(this);
    this.debugType = debugType;
    update();
  }

  public DebugLabel update() {
    String s = ClientDebug.get(debugType);
    if (s != null) {
      setText(s);
    }
    return this;
  }

  public DebugLabel resize() {
    this.setBounds(0, Gdx.graphics.getHeight() - (int) ((debugType.ordinal() + .5) * LINE_SPACING), Gdx.graphics.getWidth(), 0);
    return this;
  }

  public static void resizeAll() {
    try {
      for (DebugLabel label : labels) {
        label.resize();
      }
    } catch (Exception e) {

    }
  }

  public void validate() {
    this.update();
    super.validate();
  }

}

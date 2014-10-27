package ethanjones.modularworld.core.settings.type;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import ethanjones.data.DataGroup;
import ethanjones.modularworld.core.settings.Setting;
import ethanjones.modularworld.core.settings.VisualSettingManager;
import ethanjones.modularworld.core.system.ModularWorldException;
import ethanjones.modularworld.graphics.menu.Menu;

public class IntegerSetting extends Setting {

  private int i;
  private boolean hasRange;
  private Type type;
  private int rangeStart;
  private int rangeEnd;

  public IntegerSetting() {
    this(0);
  }

  public IntegerSetting(int i) {
    this.i = i;
    this.hasRange = false;
    this.rangeStart = 0;
    this.rangeEnd = 0;
    this.type = Type.TextField;
  }

  public IntegerSetting(int i, int rangeStart, int rangeEnd) {
    this(i, rangeStart, rangeEnd, Type.Slider);
  }

  public IntegerSetting(int i, int rangeStart, int rangeEnd, Type type) {
    this.i = i;
    this.hasRange = true;
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
    this.type = type;
  }

  public int get() {
    return i;
  }

  public void set(int i) {
    this.i = i;
  }

  @Override
  public Actor getActor(VisualSettingManager visualSettingManager) {
    switch (type) {
      case TextField:
        return getTextField();
      case Slider:
        return getSlider();
    }
    return null;
  }

  @Override
  public String toString() {
    return Integer.toString(i);
  }

  @Override
  public DataGroup write() {
    DataGroup dataGroup = new DataGroup();
    dataGroup.setInteger("data", i);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    i = data.getInteger("data");
  }

  private TextField getTextField() {
    TextField textField = new TextField(i + "", Menu.skin) {

      boolean wasFocused = false;
      String oldText = i + "";

      public void draw(Batch batch, float parentAlpha) {
        boolean focused = getStage() != null && getStage().getKeyboardFocus() == this;
        if (wasFocused && !focused) {
          try {
            int n = Integer.parseInt(getText());
            if (hasRange && (n < rangeStart || n > rangeEnd)) {
              throw new NumberFormatException();
            }
          } catch (Exception e) {
            setText(oldText);
          }
        }
        wasFocused = focused;
        super.draw(batch, parentAlpha);
      }
    };
    textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
    if (hasRange) textField.setMessageText("(" + rangeStart + "-" + rangeEnd + ")");
    return textField;
  }

  public Slider getSlider() {
    if (!hasRange) throw new ModularWorldException("Range required");
    final Slider slider = new Slider(rangeStart, rangeEnd, 1f, false, Menu.skin);
    slider.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        int n = (int) slider.getValue();
        if (n < rangeStart || n > rangeEnd) {
          return false;
        }
        i = n;
        return true;
      }
    });
    return slider;
  }

  public static enum Type {
    TextField, Slider;
  }
}

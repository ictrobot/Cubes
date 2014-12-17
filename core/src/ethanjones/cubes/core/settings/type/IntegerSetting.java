package ethanjones.cubes.core.settings.type;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import ethanjones.data.DataGroup;

import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menus.SettingsMenu;

public class IntegerSetting extends Setting {

  public static enum Type {
    TextField, Slider
  }
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

  private TextField getTextField() {
    final TextField textField = new TextField(i + "", Menu.skin);
    textField.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof SettingsMenu.SaveEvent)) return false;
        try {
          int n = Integer.parseInt(textField.getText());
          if (hasRange && (n < rangeStart || n > rangeEnd)) {
            throw new NumberFormatException();
          } else {
            i = n;
            return true;
          }
        } catch (Exception e) {
          textField.setText(i + "");
          return false;
        }
      }
    });
    textField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
    if (hasRange) textField.setMessageText("(" + rangeStart + "-" + rangeEnd + ")");
    return textField;
  }

  public Slider getSlider() {
    if (!hasRange) throw new CubesException("Range required");
    final Slider slider = new Slider(rangeStart, rangeEnd, 1f, false, Menu.skin);
    slider.setValue(i);
    slider.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof SettingsMenu.SaveEvent)) return false;
        int n = (int) slider.getValue();
        if (n < rangeStart || n > rangeEnd) {
          slider.setValue(i);
          return false;
        }
        i = n;
        return true;
      }
    });
    slider.addListener(new InputListener() { //Fixes Slider not working in a ScrollPane because it stops touchDown events reaching the ScrollPane
      public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        event.stop();
        return false;
      }
    });
    return slider;
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
    if (hasRange) MathUtils.clamp(i, rangeStart, rangeEnd);
  }
}

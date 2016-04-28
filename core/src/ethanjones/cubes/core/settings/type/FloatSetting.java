package ethanjones.cubes.core.settings.type;

import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.menu.Menu;
import ethanjones.cubes.graphics.menus.SettingsMenu;
import ethanjones.data.DataGroup;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class FloatSetting extends Setting {

  public static enum Type {
    TextField, Slider
  }

  public float f;
  public boolean hasRange;
  private Type type;
  public float rangeStart;
  public float rangeEnd;
  public float sliderSteps = 0.05f;

  public FloatSetting() {
    this(0);
  }

  public FloatSetting(float f) {
    this.f = f;
    this.hasRange = false;
    this.rangeStart = 0;
    this.rangeEnd = 0;
    this.type = Type.TextField;
  }

  public FloatSetting(float f, float rangeStart, float rangeEnd) {
    this(f, rangeStart, rangeEnd, Type.Slider);
  }

  public FloatSetting(float f, float rangeStart, float rangeEnd, Type type) {
    this.f = f;
    this.hasRange = true;
    this.rangeStart = rangeStart;
    this.rangeEnd = rangeEnd;
    this.type = type;
  }

  public float get() {
    return f;
  }

  public void set(float i) {
    this.f = i;
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
    return Float.toString(f);
  }

  private TextField getTextField() {
    final TextField textField = new TextField(f + "", Menu.skin);
    textField.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof SettingsMenu.SaveEvent)) return false;
        try {
          float n = Float.parseFloat(textField.getText());
          if (hasRange && (n < rangeStart || n > rangeEnd)) {
            throw new NumberFormatException();
          } else {
            f = n;
            return true;
          }
        } catch (Exception e) {
          textField.setText(f + "");
          return false;
        }
      }
    });
    textField.setTextFieldFilter(new TextField.TextFieldFilter() {
      @Override
      public boolean acceptChar(TextField textField, char c) {
        return c == '.' || Character.isDigit(c);
      }
    });
    if (hasRange) textField.setMessageText("(" + rangeStart + "-" + rangeEnd + ")");
    return textField;
  }

  public Slider getSlider() {
    if (!hasRange) throw new CubesException("Range required");
    final Slider slider = new Slider(rangeStart, rangeEnd, sliderSteps, false, Menu.skin);
    slider.setValue(f);
    slider.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof SettingsMenu.SaveEvent)) return false;
        float n = slider.getValue();
        if (n < rangeStart || n > rangeEnd) {
          slider.setValue(f);
          return false;
        }
        f = n;
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
    dataGroup.put("data", f);
    return dataGroup;
  }

  @Override
  public void read(DataGroup data) {
    f = data.getFloat("data");
    if (hasRange) MathUtils.clamp(f, rangeStart, rangeEnd);
  }
}

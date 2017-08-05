package ethanjones.cubes.core.settings.type;

import ethanjones.cubes.core.settings.Setting;
import ethanjones.cubes.core.settings.VisualSettingManager;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.menu.Fonts;
import ethanjones.cubes.graphics.menus.SettingsMenu;
import ethanjones.cubes.side.client.ClientDebug;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonValue;

public class IntegerSetting extends Setting {

  public enum Type {
    TextField, Slider
  }

  public int i;
  public boolean hasRange;
  private Type type;
  public int rangeStart;
  public int rangeEnd;

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
    onChange();
  }

  @Override
  public Actor getActor(VisualSettingManager visualSettingManager) {
    switch (type) {
      case TextField:
        return getTextField(visualSettingManager);
      case Slider:
        return getSlider(visualSettingManager);
    }
    return null;
  }

  @Override
  public String toString() {
    return Integer.toString(i);
  }
  
  private TextField getTextField(VisualSettingManager visualSettingManager) {
    final TextField textField = new TextField(i + "", visualSettingManager.getSkin());
    textField.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof SettingsMenu.SaveEvent)) return false;
        try {
          int n = Integer.parseInt(textField.getText());
          if (hasRange && (n < rangeStart || n > rangeEnd)) {
            throw new NumberFormatException();
          } else {
            set(n);
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

  public Slider getSlider(VisualSettingManager visualSettingManager) {
    if (!hasRange) throw new CubesException("Range required");
    final Slider slider = new SliderWithValue(rangeStart, rangeEnd, 1f, false, visualSettingManager.getSkin(), false);
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
        set(n);
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
  public JsonValue toJson() {
    return Json.value(this.i);
  }
  
  @Override
  public void readJson(JsonValue json) {
    this.i = json.asInt();
    if (hasRange) MathUtils.clamp(i, rangeStart, rangeEnd);
    onChange();
  }
  
  public static class SliderWithValue extends Slider {
  
    private final boolean dp;
  
    public SliderWithValue(float min, float max, float stepSize, boolean vertical, Skin skin, boolean dp) {
      super(min, max, stepSize, vertical, skin);
      this.dp = dp;
    }
  
    @Override
    public void draw(Batch batch, float parentAlpha) {
      super.draw(batch, parentAlpha);
      BitmapFontCache cache = Fonts.hud.getCache();
      cache.clear();
      GlyphLayout layout = cache.addText("" + (dp ? ClientDebug.twoDP(getValue()) : ((int) getValue())), getX(), getY(), getWidth(), Align.center, false);
      cache.translate(0, (layout.height / 2) + (getHeight() / 2));
      cache.draw(batch);
    }
  }
}

package ethanjones.modularworld.core.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class DebugHud {
  
  public static String lineA = "ABC";
  public static String lineB = "DEF";
  
  public static FieldLabel[] getLabels(Skin skin) {
    return labelFromFields(skin, "lineA", "lineB");
  }
  
  private static FieldLabel[] labelFromFields(Skin skin, String... fields) {
    FieldLabel[] l = new FieldLabel[fields.length];
    for (int i = 0; i < fields.length; i++) {
      try {
        l[i] = new FieldLabel(fields[i], skin, i);
      } catch (Exception e) {
        throw new DebugException("Failed to build debug screen", e);
      }
    }
    return l;
  }
  
  public static class FieldLabel extends Label {
    
    private static ArrayList<FieldLabel> labels = new ArrayList<FieldLabel>();
    private static int LINE_SPACING = 15;
    
    private Field field;
    private int i;
    
    public FieldLabel(String field, Skin skin, int i) throws Exception {
      this(DebugHud.class.getField(field), skin, i);
    }
    
    public FieldLabel(Field field, Skin skin, int i) throws Exception {
      super((CharSequence) field.get(null), skin);
      labels.add(this);
      this.field = field;
      this.i = i;
      this.resize();
    }
    
    public FieldLabel update() throws Exception {
      setText((CharSequence) field.get(null));
      return this;
    }
    
    public FieldLabel resize() {
      this.setBounds(0, Gdx.graphics.getHeight() - (int) ((i + .5) * LINE_SPACING), Gdx.graphics.getWidth(), 0);
      return this;
    }
    
    public static void updateAll() {
      try {
        for (FieldLabel label : labels) {
          label.update();
        }
      } catch (Exception e) {
        
      }
    }
    
    public static void resizeAll() {
      try {
        for (FieldLabel label : labels) {
          label.resize();
        }
      } catch (Exception e) {
        
      }
    }
  }
  
  public static class DebugException extends RuntimeException {
    
    public DebugException(String string, Exception e) {
      super(string, e);
    }
    
    private static final long serialVersionUID = 1L;
    
  }
}

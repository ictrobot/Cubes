package ethanjones.modularworld.graphics.menu.actor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class ResizableTextField extends TextField {

  private float fontScaleX = 1, fontScaleY = 1;

  public ResizableTextField(String text, Skin skin) {
    super(text, skin);
  }

  public ResizableTextField(String text, Skin skin, String styleName) {
    super(text, skin, styleName);
  }

  public ResizableTextField(String text, TextFieldStyle style) {
    super(text, style);
  }

  public void setFontScale(float fontScale) {
    this.fontScaleX = fontScale;
    this.fontScaleY = fontScale;
    invalidateHierarchy();
  }

  public void setFontScale(float fontScaleX, float fontScaleY) {
    this.fontScaleX = fontScaleX;
    this.fontScaleY = fontScaleY;
    invalidateHierarchy();
  }

  public float getFontScaleX() {
    return fontScaleX;
  }

  public void setFontScaleX(float fontScaleX) {
    this.fontScaleX = fontScaleX;
    invalidateHierarchy();
  }

  public float getFontScaleY() {
    return fontScaleY;
  }

  public void setFontScaleY(float fontScaleY) {
    this.fontScaleY = fontScaleY;
    invalidateHierarchy();
  }

  @Override
  public void invalidateHierarchy() {
    getStyle().font.setScale(Math.max(fontScaleX, 0.1f), Math.max(fontScaleY, 0.1f));
    textHeight = getStyle().font.getCapHeight() - getStyle().font.getDescent() * 2;
    super.invalidateHierarchy();
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    super.draw(batch, parentAlpha);
  }

}

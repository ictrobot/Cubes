package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFont.HAlignment;
import com.badlogic.gdx.scenes.scene2d.utils.Align;

import ethanjones.cubes.graphics.gui.Fonts;

public class GuiString extends TypeGuiElement<String> {

  protected BitmapFont font;
  private boolean multiline = false;
  private int align = Align.left;

  public GuiString(String s) {
    this(s, Fonts.Default);
  }

  public GuiString(String s, BitmapFont font) {
    super(s);
    this.font = font;
  }

  @Override
  public void render(Batch batch) {
    if (multiline) {
      font.drawMultiLine(batch, t, x.get(), y.get(), 0, getHAlignment());
    } else {
      font.draw(batch, t, x.get(), y.get());
    }
  }

  public boolean isMultiline() {
    return multiline;
  }

  public void setMultiline(boolean multiline) {
    this.multiline = multiline;
  }

  public int getAlign() {
    return align;
  }

  public HAlignment getHAlignment() {
    if ((align & Align.left) != 0) {
      return HAlignment.LEFT;
    } else if ((align & Align.right) != 0) {
      return HAlignment.RIGHT;
    } else {
      return HAlignment.CENTER;
    }
  }

  public void setAlign(int align) {
    this.align = align;
  }
}

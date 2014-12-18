package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import ethanjones.cubes.graphics.gui.Fonts;

public class GuiString extends TypeGuiElement<String> {

  protected BitmapFont font;

  public GuiString(String s) {
    this(s, Fonts.Default);
  }

  public GuiString(String s, BitmapFont font) {
    super(s);
    this.font = font;
  }

  @Override
  public void render(Batch batch) {
    Fonts.draw(t, font, x.get(), y.get(), width.get(), height.get());
  }

  public BitmapFont getFont() {
    return font;
  }

  public void setFont(BitmapFont font) {
    this.font = font;
  }
}

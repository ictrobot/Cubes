package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import ethanjones.cubes.graphics.gui.Fonts;

public class GuiString extends ResizableGuiElement {

  private String string;
  private BitmapFont font;

  public GuiString(String string) {
    this(string, Fonts.Default);
  }

  public GuiString(String string, BitmapFont font) {
    this.string = string;
    this.font = font;
  }

  @Override
  public void render(Batch batch) {
    Fonts.draw(string, font, x.get(), y.get(), width.get(), height.get());
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public BitmapFont getFont() {
    return font;
  }

  public void setFont(BitmapFont font) {
    this.font = font;
  }
}

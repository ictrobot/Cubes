package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import ethanjones.cubes.graphics.gui.Fonts;
import ethanjones.cubes.graphics.gui.Gui;

public class GuiButton extends SimpleGuiElement {

  public static interface ButtonListener {

    public void buttonDown();

    public void buttonUp();

  }

  protected String text;
  protected BitmapFont font;
  protected ButtonListener buttonListener;
  protected boolean down = false;

  public GuiButton(String text) {
    this(text, null);
  }

  public GuiButton(String text, ButtonListener buttonListener) {
    this.text = text;
    this.font = Fonts.Default;
    this.buttonListener = buttonListener;
  }

  @Override
  public void render(Batch batch) {
    if (down) {
      Gui.buttonDown.draw(batch, x.get(), y.get(), width.get(), height.get());
    } else {
      Gui.buttonUp.draw(batch, x.get(), y.get(), width.get(), height.get());
    }
    Fonts.draw(text, font, x.get(), y.get(), width.get(), height.get());
  }

  public ButtonListener getButtonListener() {
    return buttonListener;
  }

  public void setButtonListener(ButtonListener buttonListener) {
    this.buttonListener = buttonListener;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  @Override
  public boolean onButtonDown(int x, int y, int button) {
    if (x >= this.x.get() && x <= (this.x.get() + this.width.get())) {
      if (y >= this.y.get() && y <= (this.y.get() + this.height.get())) {
        buttonListener.buttonDown();
        down = true;
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean onButtonUp(int x, int y, int button) {
    //if (x >= this.x && x <= (this.x + this.width)) {
    //  if (y >= this.y && y <= (this.y + this.height)) {
    if (down) {
      buttonListener.buttonUp();
      down = false;
      return true;
    }
    //  }
    //}
    return false;
  }

  public BitmapFont getFont() {
    return font;
  }

  public void setFont(BitmapFont font) {
    this.font = font;
  }
}

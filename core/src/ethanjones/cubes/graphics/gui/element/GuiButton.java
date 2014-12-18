package ethanjones.cubes.graphics.gui.element;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import ethanjones.cubes.graphics.gui.Fonts;
import ethanjones.cubes.graphics.gui.Gui;
import ethanjones.cubes.graphics.gui.element.event.GuiEvent;
import ethanjones.cubes.graphics.gui.element.event.TypeGuiEventListener;
import ethanjones.cubes.graphics.gui.element.event.mouse.MouseDownEvent;
import ethanjones.cubes.graphics.gui.element.event.mouse.MouseUpEvent;

public class GuiButton extends ResizableGuiElement {

  public static class ButtonDownEvent extends GuiEvent {

    public final GuiButton guiButton;

    public ButtonDownEvent(GuiButton guiButton) {
      this.guiButton = guiButton;
    }
  }

  public static class ButtonUpEvent extends GuiEvent {

    public final GuiButton guiButton;

    public ButtonUpEvent(GuiButton guiButton) {
      this.guiButton = guiButton;
    }
  }

  protected String text;
  protected BitmapFont font;
  protected boolean down = false;

  public GuiButton(String text) {
    this.text = text;
    this.font = Fonts.Default;

    final GuiButton guiButton = this;
    addEventListener(new TypeGuiEventListener<MouseDownEvent>(MouseDownEvent.class) {
      @Override
      public boolean onTypeEvent(MouseDownEvent event) {
        if (!down) {
          fireEvent(new ButtonDownEvent(guiButton));
          down = true;
          return true;
        }
        return false;
      }
    });
    addEventListener(new TypeGuiEventListener<MouseUpEvent>(MouseUpEvent.class) {
      @Override
      public boolean onTypeEvent(MouseUpEvent event) {
        if (down) {
          fireEvent(new ButtonUpEvent(guiButton));
          down = false;
          return true;
        }
        return false;
      }
    });
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

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public BitmapFont getFont() {
    return font;
  }

  public void setFont(BitmapFont font) {
    this.font = font;
  }
}

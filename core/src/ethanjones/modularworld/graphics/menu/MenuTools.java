package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;
import ethanjones.modularworld.graphics.menu.actor.ResizableTextField;

public class MenuTools {

  public static enum Direction {
    Above, Below, Left, Right;
  }

  public static void move(Actor move, Direction direction, Actor other) {
    move(move, direction, other, 0);
  }

  public static void move(Actor move, Direction direction, Actor other, float padding) {
    switch (direction) {
      case Above:
        move.setX(other.getX());
        move.setY(other.getY() + other.getHeight() + padding);
        break;
      case Below:
        move.setX(other.getX());
        move.setY(other.getY() - other.getHeight() - padding);
        break;
      case Left:
        move.setX(other.getX() - other.getWidth() - padding);
        move.setY(other.getX());
        break;
      case Right:
        move.setX(other.getX() + other.getWidth() + padding);
        move.setY(other.getX());
        break;
    }
  }

  public static void arrange(float x, float y, float width, float height, Direction direction, Actor... actors) {
    switch (direction) {
      case Above:
      case Below:
        height = height / actors.length;
        break;
      case Left:
      case Right:
        width = width / actors.length;
        break;
    }
    setSize(width, height, actors);
    Actor prev = actors[0];
    setPos(x, y, prev);
    for (int i = 1; i < actors.length; i++) {
      Actor curr = actors[i];
      move(curr, direction, prev);
      prev = curr;
    }
  }

  public static void setTitle(Actor actor) {
    actor.setBounds(0, Gdx.graphics.getHeight() / 6 * 5, Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 6);
    if (actor instanceof Label) {
      ((Label) actor).setAlignment(Align.center, Align.center);
      fitText((Label) actor);
    } else if (actor instanceof TextButton) {
      ((TextButton) actor).getLabel().setAlignment(Align.center, Align.center);
      fitText((TextButton) actor);
    }
  }

  public static void copySize(Actor main, Actor... others) {
    setSize(main.getWidth(), main.getHeight(), others);
  }

  public static void setSize(float width, float height, Actor... actors) {
    for (Actor o : actors) {
      o.setWidth(width);
      o.setHeight(height);
    }
  }

  public static void copyPos(Actor main, Actor... others) {
    setPos(main.getX(), main.getY(), others);
  }

  public static void setPos(float x, float y, Actor... actors) {
    for (Actor o : actors) {
      o.setX(x);
      o.setY(y);
    }
  }

  public static void copyPosAndSize(Actor main, Actor... others) {
    setPos(main.getX(), main.getY(), others);
    setSize(main.getWidth(), main.getHeight(), others);
  }

  public static void fitText(Object... objects) {
    float scale = -1;
    for (Object o : objects) {
      if (o instanceof TextButton) {
        scale = scale == -1 ? getTextFitScale((TextButton) o) : Math.min(scale, getTextFitScale((TextButton) o));
      } else if (o instanceof Label) {
        scale = scale == -1 ? getTextFitScale((Label) o) : Math.min(scale, getTextFitScale((Label) o));
      } else if (o instanceof ResizableTextField) {
        scale = scale == -1 ? getTextFitScale((ResizableTextField) o) : Math.min(scale, getTextFitScale((ResizableTextField) o));
      }
    }
    for (Object o : objects) {
      if (o instanceof TextButton) {
        setTextScale(scale, (TextButton) o);
      } else if (o instanceof Label) {
        setTextScale(scale, (Label) o);
      } else if (o instanceof ResizableTextField) {
        setTextScale(scale, (ResizableTextField) o);
      }
    }
  }

  public static void fitTextSeperately(TextButton... textButtons) {
    for (TextButton textButton : textButtons) {
      setTextScale(getTextFitScale(textButton), textButtons);
    }
  }

  public static void fitTextSeperately(Label... labels) {
    for (Label label : labels) {
      setTextScale(getTextFitScale(label), label);
    }
  }

  public static void fitTextSeperately(ResizableTextField... resizableTextFields) {
    for (ResizableTextField resizableTextField : resizableTextFields) {
      setTextScale(getTextFitScale(resizableTextField), resizableTextField);
    }
  }

  public static void setTextScale(float size, TextButton... textButtons) {
    for (TextButton textButton : textButtons) {
      textButton.getLabel().setFontScale(size);
    }
  }

  public static void setTextScale(float size, Label... labels) {
    for (Label label : labels) {
      label.setFontScale(size);
    }
  }

  public static void setTextScale(float size, ResizableTextField... resizableTextFields) {
    for (ResizableTextField resizableTextField : resizableTextFields) {
      resizableTextField.setFontScale(size);
    }
  }

  public static float getTextFitScale(Label label) {
    float prevX = label.getStyle().font.getScaleX();
    float prevY = label.getStyle().font.getScaleY();
    label.getStyle().font.setScale(1);
    BitmapFont.TextBounds bounds = label.getStyle().font.getBounds(label.getText());
    label.getStyle().font.setScale(prevX, prevY);
    return Math.min((label.getWidth() - 16) / bounds.width, (label.getHeight() - 16) / bounds.height);
  }

  public static float getTextFitScale(TextButton textButton) {
    float prevX = textButton.getLabel().getStyle().font.getScaleX();
    float prevY = textButton.getLabel().getStyle().font.getScaleY();
    textButton.getLabel().getStyle().font.setScale(1);
    BitmapFont.TextBounds bounds = textButton.getLabel().getStyle().font.getBounds(textButton.getText());
    textButton.getLabel().getStyle().font.setScale(prevX, prevY);
    return Math.min((textButton.getWidth() - 16) / bounds.width, (textButton.getHeight() - 16) / bounds.height);
  }

  /**
   * @param resizableTextField must have message set
   */
  public static float getTextFitScale(ResizableTextField resizableTextField) {
    float prevX = resizableTextField.getStyle().font.getScaleX();
    float prevY = resizableTextField.getStyle().font.getScaleY();
    resizableTextField.getStyle().font.setScale(1);
    BitmapFont.TextBounds bounds = resizableTextField.getStyle().font.getBounds(resizableTextField.getMessageText());
    resizableTextField.getStyle().font.setScale(prevX, prevY);
    return Math.min((resizableTextField.getWidth() - 16) / bounds.width, (resizableTextField.getHeight() - 16) / bounds.height);
  }

  public static TextButton getBackButton(final Menu menu) {
    TextButton textButton = new TextButton(Localization.get("menu.main.back"), Menu.skin);
    textButton.addListener(new EventListener() {
      @Override
      public boolean handle(Event event) {
        if (!(event instanceof ChangeListener.ChangeEvent)) return false;
        Menu prev = MenuManager.getPrevious(menu);
        if (prev == null) return false;
        GraphicalAdapter.instance.setMenu(prev);
        return true;
      }
    });
    return textButton;
  }
}
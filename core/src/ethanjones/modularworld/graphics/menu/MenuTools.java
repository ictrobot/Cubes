package ethanjones.modularworld.graphics.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import ethanjones.modularworld.core.adapter.GraphicalAdapter;
import ethanjones.modularworld.core.localization.Localization;

public class MenuTools {

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
    if (direction == Direction.Below) {
      y -= height;
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

  public static TextButton getBackButton(final Menu menu) {
    TextButton textButton = new TextButton(Localization.get("menu.general.back"), Menu.skin);
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

  public static enum Direction {
    Above, Below, Left, Right;
  }
}
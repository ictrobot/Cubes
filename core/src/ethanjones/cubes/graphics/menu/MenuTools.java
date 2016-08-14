package ethanjones.cubes.graphics.menu;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.platform.Adapter;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Align;

import static ethanjones.cubes.graphics.Graphics.GUI_HEIGHT;
import static ethanjones.cubes.graphics.Graphics.GUI_WIDTH;

public class MenuTools {

  public static enum Direction {
    Above, Below, Left, Right
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

  public static void setSize(float width, float height, Actor... actors) {
    for (Actor o : actors) {
      o.setSize(width, height);
    }
  }

  public static void setPos(float x, float y, Actor... actors) {
    for (Actor o : actors) {
      o.setPosition(x, y);
    }
  }

  public static void setMaxPrefSize(Layout... actors) {
    float width = 0f, height = 0f;
    for (Layout actor : actors) {
      if (actor.getPrefWidth() > width) width = actor.getPrefWidth();
      if (actor.getPrefHeight() > height) height = actor.getPrefHeight();
    }
    for (Layout actor : actors) {
      ((Actor) actor).setSize(width, height);
    }
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

  public static void setTitle(Label label) {
    label.setBounds(0, GUI_HEIGHT / 6 * 5, GUI_WIDTH, GUI_HEIGHT / 6);
    label.setAlignment(Align.center, Align.center);
  }

  public static void copySize(Actor main, Actor... others) {
    setSize(main.getWidth(), main.getHeight(), others);
  }

  public static void copyPos(Actor main, Actor... others) {
    setPos(main.getX(), main.getY(), others);
  }

  public static void copyPosAndSize(Actor main, Actor... others) {
    setPos(main.getX(), main.getY(), others);
    setSize(main.getWidth(), main.getHeight(), others);
  }

  public static void arrangeX(float y, boolean centerY, Actor... actors) {
    float w = (GUI_WIDTH / (actors.length + 1));
    for (int i = 0; i < actors.length; i++) {
      Actor a = actors[i];
      a.setPosition(((i + 1) * w) - (a.getWidth() / 2f), y - (centerY ? (a.getWidth() / 2f) : 0));
    }
  }

  public static void arrangeY(float x, boolean centerX, Actor... actors) {
    float w = (GUI_HEIGHT / (actors.length + 1));
    for (int i = 0; i < actors.length; i++) {
      Actor a = actors[i];
      a.setPosition(x - (centerX ? (a.getWidth() / 2f) : 0), ((i + 1) * w) - (a.getHeight() / 2f));
    }
  }

  public static void center(Actor actor) {
    actor.setPosition((GUI_WIDTH / 2) - (actor.getWidth() / 2), (GUI_HEIGHT / 2) - (actor.getHeight() / 2));
  }

  public static TextButton getBackButton(final Menu menu) {
    TextButton textButton = new TextButton(Localization.get("menu.general.back"), Menu.skin);
    textButton.addListener(new ChangeListener() {
      @Override
      public void changed(ChangeEvent event, Actor actor) {
        Menu prev = MenuManager.getPrevious(menu);
        if (prev == null) return;
        Adapter.setMenu(prev);
      }
    });
    return textButton;
  }
}
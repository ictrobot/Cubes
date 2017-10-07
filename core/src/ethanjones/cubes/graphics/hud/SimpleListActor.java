package ethanjones.cubes.graphics.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Call invalidate() after updating the collection
 */
public class SimpleListActor<T> extends Widget {

  protected float prefHeight;
  protected float itemHeight;
  protected float prefWidth;
  protected float gap;
  protected Collection<T> items = new ArrayList<T>();
  protected final BitmapFont font;

  public SimpleListActor(BitmapFont bitmapFont) {
    this.font = bitmapFont;
    setSize(getPrefWidth(), getPrefHeight());
  }

  public void layout() {
    itemHeight = font.getCapHeight() - (font.getDescent() * 2) + gap;
    prefHeight = items.size() * itemHeight;
    prefWidth = 0;
    GlyphLayout layout = new GlyphLayout();
    for (T item : items) {
      layout.setText(font, item.toString());
      prefWidth = Math.max(layout.width, prefWidth);
    }
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    validate();

    Color color = getColor();
    batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

    float currentY = itemHeight;
    for (T item : items) {
      font.draw(batch, item.toString(), getX(), getY() + currentY);
      currentY += itemHeight;
    }
  }

  public void setCollection(Collection<T> items) {
    if (items == null) throw new IllegalArgumentException("items cannot be null");
    this.items = items;
    invalidate();
  }

  public float getItemHeight() {
    return itemHeight;
  }

  public float getPrefWidth() {
    validate();
    return prefWidth;
  }

  public float getPrefHeight() {
    validate();
    return prefHeight;
  }

  public float getGap() {
    return gap;
  }

  public void setGap(float gap) {
    if (gap < 0) throw new IllegalArgumentException("gap must be >=  0");
    this.gap = gap;
    invalidate();
  }
}


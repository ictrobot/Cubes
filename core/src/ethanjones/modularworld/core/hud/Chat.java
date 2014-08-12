package ethanjones.modularworld.core.hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.PackedTexture;

public final class Chat extends Label {

  private static class Bounds {
    float x;
    float y;
    float width;
    float height;

    public void set(float x, float y, float width, float height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }
  }

  protected Chat(Skin skin) {
    super("", skin);
    chatBackground = GraphicsHelper.getTexture("hud/ChatBackground.png");
    bounds = new Bounds();
  }

  PackedTexture chatBackground;
  Bounds bounds;
  String string = "";

  public void resize() {
    this.setText(" ");
    bounds.set(0, 0, Gdx.graphics.getWidth(), this.getTextBounds().height);
    this.setBounds(bounds.x, bounds.y, bounds.width, bounds.height);
  }

  @Override
  public void draw(Batch batch, float parentAlpha) {
    if (ChatManager.visible) {
      resize();
      this.setText(string);
      batch.draw(chatBackground.textureRegion, bounds.x, bounds.y, bounds.width, bounds.height);
      super.draw(batch, parentAlpha);
    }
  }
}

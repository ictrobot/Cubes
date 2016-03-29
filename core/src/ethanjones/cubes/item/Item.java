package ethanjones.cubes.item;

import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Item {

  public String id;

  protected TextureRegion texture;

  public Item(String id) {
    if (!id.contains(":")) throw new IllegalArgumentException(id + " is not in the correct format");
    this.id = id;
  }

  public void loadGraphics() {
    TextureRegion textureRegion = Assets.getPackedTexture(id + ".png");
    if (textureRegion == null) {
      int index = id.indexOf(":");
      textureRegion = Assets.getPackedTexture(id.substring(0, index) + ":item/" + id.substring(index + 1) + ".png");
      if (textureRegion == null) {
        throw new CubesException("Can't find item texture for " + id);
      }
    }
    this.texture = textureRegion;
  }

  public TextureRegion getTextureRegion() {
    return texture;
  }

  public String getName() {
    return Localization.get("item." + id.replaceFirst(":", "."));
  }
}

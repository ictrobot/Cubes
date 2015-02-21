package ethanjones.cubes.client.graphics.assets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class PackedTexture {

  public final Texture texture;
  public final TextureRegion textureRegion;

  public PackedTexture(Texture texture, TextureRegion textureRegion) {
    this.texture = texture;
    this.textureRegion = textureRegion;
  }

}

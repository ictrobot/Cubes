package ethanjones.modularworld.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;

public class PackedTexture {
  public final Texture packedTexture;
  public final int number;
  public final Material material;
  public final TextureRegion textureRegion;
  public final String filename;

  public PackedTexture(Texture packedTexture, int number, Material material, TextureRegion textureRegion, String filename) {
    this.packedTexture = packedTexture;
    this.number = number;
    this.material = material;
    this.textureRegion = textureRegion;
    this.filename = filename;
  }
}

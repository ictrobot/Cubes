package ethanjones.modularworld.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class PackedTexture {
  public final Texture packedTexture;
  public final int number;
  public final PackedMaterial material;
  public final TextureRegion textureRegion;
  public final String filename;

  public PackedTexture(Texture packedTexture, int number, PackedMaterial material, TextureRegion textureRegion, String filename) {
    this.packedTexture = packedTexture;
    this.number = number;
    this.material = material;
    this.textureRegion = textureRegion;
    this.filename = filename;
  }

  public static class PackedMaterial extends Material {
    public PackedMaterial(TextureAttribute... textureAttribute) {
      super(textureAttribute);
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof PackedMaterial)) return false;
      return id.equals(((PackedMaterial) other).id) && attributes.equals(((PackedMaterial) other).attributes);
    }

    public String getID() {
      return id;
    }
  }
}

package ethanjones.cubes.graphics.assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;

import java.util.HashMap;

public class PackedTextureSheet {

  private final HashMap<String, TextureRegion> packedTextures;
  private final Material material;

  public PackedTextureSheet(Material material) {
    packedTextures = new HashMap<String, TextureRegion>();
    this.material = material;
  }

  public HashMap<String, TextureRegion> getPackedTextures() {
    return packedTextures;
  }

  public Material getMaterial() {
    return material;
  }

  public TextureRegion getPackedTexture(String name) {
    return packedTextures.get(name);
  }
}

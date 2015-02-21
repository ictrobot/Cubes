package ethanjones.cubes.client.graphics.assets;

import com.badlogic.gdx.graphics.g3d.Material;
import java.util.HashMap;

public class PackedTextureSheet {

  private final HashMap<String, PackedTexture> packedTextures;
  private final Material material;

  public PackedTextureSheet(Material material) {
    packedTextures = new HashMap<String, PackedTexture>();
    this.material = material;
  }

  public HashMap<String, PackedTexture> getPackedTextures() {
    return packedTextures;
  }

  public Material getMaterial() {
    return material;
  }
}

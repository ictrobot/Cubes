package ethanjones.modularworld.graphics.block;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.PackedTexture;

public class BlockTextureHandler {

  TextureRegion[] textureRegions;

  public BlockTextureHandler(PackedTexture mainTexture) {
    textureRegions = new TextureRegion[6];
    if (!checkPackedTexture(mainTexture)) {
      Log.error(new ModularWorldException("Block textures have to be on block packed texture"));
    }
    for (int i = 0; i <= 5; i++) {
      textureRegions[i] = mainTexture.textureRegion; //i + ""
    }
  }

  private static boolean checkPackedTexture(PackedTexture packedTexture) {
    return packedTexture.material == GraphicsHelper.blockPackedTextures;
  }

  public TextureRegion getSide(Direction direction) {
    return textureRegions[direction.index];
  }

  public BlockTextureHandler setSide(Direction direction, PackedTexture packedTexture) {
    if (!checkPackedTexture(packedTexture)) {
      Log.error(new ModularWorldException("Block textures have to be on block packed texture"));
    }
    textureRegions[direction.index] = packedTexture.textureRegion;
    return this;
  }

}

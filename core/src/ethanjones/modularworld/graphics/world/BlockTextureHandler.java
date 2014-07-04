package ethanjones.modularworld.graphics.world;

import ethanjones.modularworld.core.ModularWorldException;
import ethanjones.modularworld.core.logging.Log;
import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.PackedTexture;

public class BlockTextureHandler {

  PackedTexture[] packedTextures;

  public BlockTextureHandler(PackedTexture mainTexture) {
    packedTextures = new PackedTexture[6];
    if (!checkPackedTexture(mainTexture)) {
      Log.error(new ModularWorldException("Block textures have to be on block packed texture"));
    }
    for (int i = 0; i <= 5; i++) {
      packedTextures[i] = mainTexture; //i + ""
    }
  }

  private static boolean checkPackedTexture(PackedTexture packedTexture) {
    return packedTexture.material == GraphicsHelper.blockPackedTextures;
  }

  public PackedTexture getSide(int direction) {
    return packedTextures[direction];
  }

  public PackedTexture getSide(Direction direction) {
    return packedTextures[direction.index];
  }

  public BlockTextureHandler setSide(Direction direction, PackedTexture packedTexture) {
    if (!checkPackedTexture(packedTexture)) {
      Log.error(new ModularWorldException("Block textures have to be on block packed texture"));
    }
    packedTextures[direction.index] = packedTexture;
    return this;
  }

}

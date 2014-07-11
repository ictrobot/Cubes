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
    checkPackedTexture(mainTexture);
    for (int i = 0; i <= 5; i++) {
      packedTextures[i] = mainTexture;
    }
  }

  private static void checkPackedTexture(PackedTexture packedTexture) {
    if (!packedTexture.material.equals(GraphicsHelper.blockPackedTextures)) {
      Log.error(new ModularWorldException("Block textures have to be on block packed texture"));
    }
  }

  public PackedTexture getSide(int direction) {
    return packedTextures[direction];
  }

  public PackedTexture getSide(Direction direction) {
    return packedTextures[direction.index];
  }

  public BlockTextureHandler setSide(Direction direction, PackedTexture packedTexture) {
    checkPackedTexture(packedTexture);
    packedTextures[direction.index] = packedTexture;
    return this;
  }

}

package ethanjones.modularworld.graphics.world;

import ethanjones.modularworld.core.util.Direction;
import ethanjones.modularworld.graphics.assets.Assets;
import ethanjones.modularworld.graphics.assets.PackedTexture;

import java.util.Arrays;

public class BlockTextureHandler {

  PackedTexture[] packedTextures;

  public BlockTextureHandler(String mainTextureName) {
    packedTextures = new PackedTexture[6];
    Arrays.fill(packedTextures, Assets.getBlockTexture(mainTextureName));
  }

  public PackedTexture getSide(Direction direction) {
    return getSide(direction.index);
  }

  public PackedTexture getSide(int direction) {
    return packedTextures[direction];
  }

  public BlockTextureHandler setSide(Direction direction, String name) {
    packedTextures[direction.index] = Assets.getBlockTexture(name);
    return this;
  }

}

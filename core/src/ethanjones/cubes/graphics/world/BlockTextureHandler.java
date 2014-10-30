package ethanjones.cubes.graphics.world;

import java.util.Arrays;

import ethanjones.cubes.core.util.Direction;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.assets.PackedTexture;

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

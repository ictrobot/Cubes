package ethanjones.cubes.graphics.world;

import java.util.Arrays;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.assets.PackedTexture;

public class BlockTextureHandler {

  PackedTexture[] packedTextures;

  public BlockTextureHandler(String mainTextureName) {
    packedTextures = new PackedTexture[6];
    Arrays.fill(packedTextures, Assets.getBlockTexture(mainTextureName));
  }

  public PackedTexture getSide(BlockFace blockFace) {
    return getSide(blockFace.index);
  }

  public PackedTexture getSide(int direction) {
    return packedTextures[direction];
  }

  public BlockTextureHandler setSide(BlockFace blockFace, String name) {
    packedTextures[blockFace.index] = Assets.getBlockTexture(name);
    return this;
  }

}

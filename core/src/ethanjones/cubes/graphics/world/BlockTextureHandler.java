package ethanjones.cubes.graphics.world;

import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.graphics.assets.Assets;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.Arrays;

public class BlockTextureHandler {

  TextureRegion[] textureRegions;

  public BlockTextureHandler(String id) {
    this(Assets.getBlockItemTextureRegion(id, "block"));
  }

  public BlockTextureHandler(TextureRegion textureRegion) {
    if (textureRegion == null) throw new CubesException("Texture region cannot be null");
    textureRegions = new TextureRegion[6];
    Arrays.fill(textureRegions, textureRegion);
  }

  public TextureRegion getSide(BlockFace blockFace) {
    return getSide(blockFace == null ? 0 : blockFace.index);
  }

  public TextureRegion getSide(int direction) {
    return textureRegions[direction];
  }

  public BlockTextureHandler setSide(BlockFace blockFace, String id) {
    textureRegions[blockFace.index] = Assets.getBlockItemTextureRegion(id, "block");
    return this;
  }

  public BlockTextureHandler setSide(int blockFace, String id) {
    textureRegions[blockFace] = Assets.getBlockItemTextureRegion(id, "block");
    return this;
  }
}

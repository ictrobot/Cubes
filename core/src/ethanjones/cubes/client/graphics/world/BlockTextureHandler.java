package ethanjones.cubes.client.graphics.world;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import java.util.Arrays;

import ethanjones.cubes.common.core.system.CubesException;
import ethanjones.cubes.common.core.util.BlockFace;
import ethanjones.cubes.client.graphics.assets.Assets;

public class BlockTextureHandler {

  TextureRegion[] textureRegions;

  public BlockTextureHandler(TextureRegion textureRegion) {
    textureRegions = new TextureRegion[6];
    Arrays.fill(textureRegions, textureRegion);
  }

  public TextureRegion getSide(BlockFace blockFace) {
    return getSide(blockFace.index);
  }

  public TextureRegion getSide(int direction) {
    return textureRegions[direction];
  }

  public BlockTextureHandler setSide(BlockFace blockFace, String name) {
    TextureRegion textureRegion = Assets.getBlockTexture(name);
    if (textureRegion == null) {
      throw new CubesException("No block texture with name \"" + name + "\"");
    }
    textureRegions[blockFace.index] = textureRegion;
    return this;
  }

}

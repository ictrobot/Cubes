package ethanjones.cubes.block;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ethanjones.cubes.block.data.BlockAttributes;
import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.core.localization.Localization;
import ethanjones.cubes.core.system.CubesException;
import ethanjones.cubes.graphics.assets.Assets;
import ethanjones.cubes.graphics.assets.PackedTexture;
import ethanjones.cubes.graphics.world.BlockTextureHandler;

public abstract class Block {

  protected String id;

  protected BlockTextureHandler textureHandler;
  protected BlockAttributes blockAttributes;

  public Block(String id) {
    if (!id.contains(":")) throw new IllegalArgumentException(id + " is not in the correct format");
    this.id = id;
    this.blockAttributes = null;
  }

  public void loadGraphics() {
    TextureRegion textureRegion = Assets.getBlockTexture(id + ".png");
    if (textureRegion == null) {
      int index = id.indexOf(":");
      textureRegion = Assets.getBlockTexture(id.substring(0, index) + ":block/" + id.substring(index + 1) + ".png");
      if (textureRegion == null) {
        throw new CubesException("Can't find block texture for " + id);
      }
    }
    textureHandler = new BlockTextureHandler(textureRegion);
  }

  public BlockTextureHandler getTextureHandler(BlockData blockData) {
    return textureHandler;
  }

  public BlockData getBlockData() {
    return blockAttributes == null ? null : new BlockData(blockAttributes);
  }

  public String getName(BlockData blockData) {
    return Localization.get("block." + id.replaceFirst(":", "."));
  }
}

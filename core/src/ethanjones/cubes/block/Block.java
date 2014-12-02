package ethanjones.cubes.block;

import ethanjones.cubes.block.data.BlockAttributes;
import ethanjones.cubes.block.data.BlockData;
import ethanjones.cubes.graphics.world.BlockTextureHandler;

public abstract class Block {

  protected BlockTextureHandler textureHandler;
  protected BlockAttributes blockAttributes;
  String mainMaterial;

  public Block(String mainMaterial) {
    this.mainMaterial = mainMaterial;
    this.blockAttributes = null;
  }

  public void loadGraphics() {
    textureHandler = new BlockTextureHandler(mainMaterial);
  }

  public BlockTextureHandler getTextureHandler(BlockData blockData) {
    return textureHandler;
  }

  public BlockData getBlockData() {
    return blockAttributes == null ? null : new BlockData(blockAttributes);
  }
}

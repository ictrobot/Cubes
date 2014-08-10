package ethanjones.modularworld.block.factory.basic;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.graphics.world.BlockTextureHandler;

public class BlockFactoryBasic extends BlockFactory {

  BlockTextureHandler textureHandler;
  String mainMaterial;

  public BlockFactoryBasic(String mainMaterial) {
    this.mainMaterial = mainMaterial;
  }

  @Override
  public void loadGraphics() {
    textureHandler = new BlockTextureHandler(mainMaterial);
  }

  @Override
  public BlockTextureHandler getTextureHandler(DataGroup data) {
    return textureHandler;
  }
}

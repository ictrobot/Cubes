package ethanjones.modularworld.block.basic;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.block.BlockTextureHandler;

public class BlockFactoryBasic extends BlockFactory {

  BlockTextureHandler textureHandler;

  public BlockFactoryBasic(String id) {
    this(id, id);
  }

  public BlockFactoryBasic(String id, String mainMaterial) {
    super(id);
    textureHandler = new BlockTextureHandler(GraphicsHelper.loadBlock(mainMaterial));
  }

  @Override
  public void loadGraphics() {

  }

  @Override
  public BlockTextureHandler getTextureHandler(ByteData data) {
    return textureHandler;
  }
}

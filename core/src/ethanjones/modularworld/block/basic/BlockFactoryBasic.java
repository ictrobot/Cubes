package ethanjones.modularworld.block.basic;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.graphics.GraphicsHelper;
import ethanjones.modularworld.graphics.block.BlockRenderHandler;
import ethanjones.modularworld.graphics.block.FullCubeRenderHandler;

public class BlockFactoryBasic extends BlockFactory {

  FullCubeRenderHandler renderer;

  public BlockFactoryBasic(String id) {
    this(id, id);
  }

  public BlockFactoryBasic(String id, String mainMaterial) {
    super(id);
    renderer = new FullCubeRenderHandler(GraphicsHelper.loadBlock(mainMaterial));
  }

  @Override
  public void loadGraphics() {
    renderer.load();
  }

  @Override
  public BlockRenderHandler getRenderer(ByteData data) {
    return renderer;
  }
}

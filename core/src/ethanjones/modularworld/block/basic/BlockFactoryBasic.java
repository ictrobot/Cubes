package ethanjones.modularworld.block.basic;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.block.rendering.BlockRenderer;
import ethanjones.modularworld.block.rendering.FullCubeRenderer;
import ethanjones.modularworld.data.ByteData;
import ethanjones.modularworld.graphics.GraphicsHelper;

public class BlockFactoryBasic extends BlockFactory {

  FullCubeRenderer renderer;

  public BlockFactoryBasic(String id) {
    this(id, id);
  }

  public BlockFactoryBasic(String id, String mainMaterial) {
    super(id);
    renderer = new FullCubeRenderer(GraphicsHelper.loadBlock(mainMaterial));
  }

  @Override
  public void loadGraphics() {
    renderer.load();
  }

  @Override
  public BlockRenderer getRenderer(ByteData data) {
    return renderer;
  }
}

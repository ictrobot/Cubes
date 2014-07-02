package ethanjones.modularworld.block;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.graphics.world.BlockTextureHandler;

/**
 * Actual Block in World
 */
public class Block {

  public final BlockFactory factory;
  protected ByteData data;

  public Block(BlockFactory factory, ByteData data) {
    this(factory);
    this.data = data;
  }

  public Block(BlockFactory factory) {
    this.factory = factory;
    this.data = new ByteData();
  }

  public BlockTextureHandler getTextureHandler() {
    return factory.getTextureHandler(data);
  }

  public boolean equals(Object o) {
    if (o instanceof Block) {
      return ((Block) o).data.equals(this.data) && ((Block) o).factory.equals(this.factory);
    }
    return false;
  }
}

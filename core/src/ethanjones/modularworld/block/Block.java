package ethanjones.modularworld.block;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.graphics.world.BlockTextureHandler;

/**
 * Actual Block in World
 */
public class Block {

  protected final BlockFactory factory;
  protected DataGroup data;

  public Block(BlockFactory factory, DataGroup data) {
    this(factory);
    this.data = data;
  }

  public Block(BlockFactory factory) {
    this.factory = factory;
    this.data = new DataGroup();
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

  public BlockFactory getFactory() {
    return factory;
  }
}

package ethanjones.modularworld.block.factory;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.data.DataGroup;
import ethanjones.modularworld.graphics.world.BlockTextureHandler;

/**
 * Setup for Block
 */
public abstract class BlockFactory {

  public final String id;
  public int numID;

  public BlockFactory(String id) {
    this.id = id;
  }

  public abstract void loadGraphics();

  public abstract BlockTextureHandler getTextureHandler(DataGroup data);

  public Block getBlock() {
    return getBlock(new DataGroup());
  }

  public Block getBlock(DataGroup data) {
    return new Block(this, data);
  }

}

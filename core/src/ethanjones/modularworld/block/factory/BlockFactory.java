package ethanjones.modularworld.block.factory;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.block.rendering.BlockRenderHandler;
import ethanjones.modularworld.core.data.ByteData;

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

  public abstract BlockRenderHandler getRenderer(ByteData data);

  public Block getBlock(ByteData data) {
    return new Block(this, data);
  }

}

package ethanjones.modularworld.block.factory;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.graphics.world.block.BlockTextureHandler;
import ethanjones.modularworld.graphics.world.block.BlockRenderer;

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

  public abstract BlockTextureHandler getTextureHandler(ByteData data);

  public BlockRenderer getCustomRenderer(ByteData data) {
    return null;
  }

  public Block getBlock() {
    return getBlock(new ByteData());
  }

  public Block getBlock(ByteData data) {
    return new Block(this, data);
  }

}

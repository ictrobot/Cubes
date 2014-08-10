package ethanjones.modularworld.block.factory;

import ethanjones.modularworld.core.data.core.DataGroup;
import ethanjones.modularworld.graphics.world.BlockTextureHandler;

/**
 * Setup for Block
 */
public abstract class BlockFactory {

  public BlockFactory() {

  }

  public abstract void loadGraphics();

  public abstract BlockTextureHandler getTextureHandler(DataGroup data);

}

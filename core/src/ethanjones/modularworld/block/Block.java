package ethanjones.modularworld.block;

import ethanjones.modularworld.ModularWorld;
import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.block.rendering.BlockRenderHandler;
import ethanjones.modularworld.core.data.ByteData;
import ethanjones.modularworld.world.World;

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

  public BlockRenderHandler getRenderer() {
    return factory.getRenderer(data);
  }

  public static boolean isCovered(int x, int y, int z) {
    if (ModularWorld.instance.world.getBlock(x + 1, y, z) == null) {
      return false;
    }
    if (ModularWorld.instance.world.getBlock(x - 1, y, z) == null) {
      return false;
    }

    if (y != World.HEIGHT_LIMIT && ModularWorld.instance.world.getBlock(x, y + 1, z) == null) {
      return false;
    }

    if (y != 0 && ModularWorld.instance.world.getBlock(x, y - 1, z) == null) {
      return false;
    }

    if (ModularWorld.instance.world.getBlock(x, y, z + 1) == null) {
      return false;
    }
    return ModularWorld.instance.world.getBlock(x, y, z - 1) != null;
  }
}

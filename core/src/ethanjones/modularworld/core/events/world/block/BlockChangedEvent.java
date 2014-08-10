package ethanjones.modularworld.core.events.world.block;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class BlockChangedEvent extends BlockEvent {

  private final BlockFactory oldBlock;

  public BlockChangedEvent(BlockCoordinates blockCoordinates, BlockFactory oldBlock) {
    super(false, blockCoordinates);
    this.oldBlock = oldBlock;
  }

  public BlockFactory getOldBlock() {
    return oldBlock;
  }
}

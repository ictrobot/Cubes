package ethanjones.modularworld.core.events.world.block;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class BlockChangedEvent extends BlockEvent {

  private final Block oldBlock;

  public BlockChangedEvent(BlockCoordinates blockCoordinates, Block oldBlock) {
    super(false, blockCoordinates);
    this.oldBlock = oldBlock;
  }

  public Block getOldBlock() {
    return oldBlock;
  }
}

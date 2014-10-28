package ethanjones.modularworld.core.events.world.block;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.world.reference.BlockReference;

public class BlockChangedEvent extends BlockEvent {

  private final Block oldBlock;

  public BlockChangedEvent(BlockReference blockReference, Block oldBlock) {
    super(false, blockReference);
    this.oldBlock = oldBlock;
  }

  public Block getOldBlock() {
    return oldBlock;
  }
}

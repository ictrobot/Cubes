package ethanjones.cubes.core.events.world.block;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.world.reference.BlockReference;

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

package ethanjones.cubes.common.core.event.world.block;

import ethanjones.cubes.common.block.Block;
import ethanjones.cubes.common.world.reference.BlockReference;

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

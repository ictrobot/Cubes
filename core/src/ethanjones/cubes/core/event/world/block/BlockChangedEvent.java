package ethanjones.cubes.core.event.world.block;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.world.reference.BlockReference;

public class BlockChangedEvent extends BlockEvent {

  private final Block oldBlock;
  private final Block newBlock;

  public BlockChangedEvent(BlockReference blockReference, Block oldBlock, Block newBlock) {
    super(false, blockReference);
    this.oldBlock = oldBlock;
    this.newBlock = newBlock;
  }

  public Block getOldBlock() {
    return oldBlock;
  }

  public Block getNewBlock() {
    return newBlock;
  }
}

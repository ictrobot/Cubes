package ethanjones.modularworld.core.events.world.block;

import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class SetBlockEvent extends BlockEvent {

  private final Block newBlock;

  public SetBlockEvent(BlockCoordinates blockCoordinates, Block newBlock) {
    super(true, blockCoordinates);
    this.newBlock = newBlock;
  }

  public Block getNewBlock() {
    return newBlock;
  }
}

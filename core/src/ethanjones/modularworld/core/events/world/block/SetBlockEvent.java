package ethanjones.modularworld.core.events.world.block;

import ethanjones.modularworld.block.factory.BlockFactory;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class SetBlockEvent extends BlockEvent {

  private final BlockFactory newBlock;

  public SetBlockEvent(BlockCoordinates blockCoordinates, BlockFactory newBlock) {
    super(true, blockCoordinates);
    this.newBlock = newBlock;
  }

  public BlockFactory getNewBlock() {
    return newBlock;
  }
}

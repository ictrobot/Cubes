package ethanjones.modularworld.core.events.world.block;

import ethanjones.modularworld.core.events.world.WorldEvent;
import ethanjones.modularworld.world.coordinates.BlockCoordinates;

public class BlockEvent extends WorldEvent {

  private final BlockCoordinates blockCoordinates;

  public BlockEvent(boolean cancelable, BlockCoordinates blockCoordinates) {
    super(cancelable);
    this.blockCoordinates = blockCoordinates;
  }

  public BlockCoordinates getBlockCoordinates() {
    return blockCoordinates;
  }
}

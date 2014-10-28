package ethanjones.modularworld.core.events.world.block;

import ethanjones.modularworld.core.events.world.WorldEvent;
import ethanjones.modularworld.world.reference.BlockReference;

public class BlockEvent extends WorldEvent {

  private final BlockReference blockReference;

  public BlockEvent(boolean cancelable, BlockReference blockReference) {
    super(cancelable);
    this.blockReference = blockReference;
  }

  public BlockReference getBlockReference() {
    return blockReference;
  }
}

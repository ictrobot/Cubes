package ethanjones.cubes.core.event.world.block;

import ethanjones.cubes.core.event.world.WorldEvent;
import ethanjones.cubes.world.reference.BlockReference;

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

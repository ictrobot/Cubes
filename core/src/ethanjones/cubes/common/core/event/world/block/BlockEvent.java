package ethanjones.cubes.common.core.event.world.block;

import ethanjones.cubes.common.core.event.world.WorldEvent;
import ethanjones.cubes.common.world.reference.BlockReference;

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

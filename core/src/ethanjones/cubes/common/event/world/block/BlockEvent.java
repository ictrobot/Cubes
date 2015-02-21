package ethanjones.cubes.common.event.world.block;

import ethanjones.cubes.common.event.world.WorldEvent;
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

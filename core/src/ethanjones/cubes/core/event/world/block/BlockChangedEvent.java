package ethanjones.cubes.core.event.world.block;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.world.reference.BlockReference;
import ethanjones.cubes.world.storage.Area;

public class BlockChangedEvent extends BlockEvent {

  private final Block oldBlock;
  private final int oldMeta;
  private final Block newBlock;
  private final int newMeta;
  private final Area area;

  public BlockChangedEvent(BlockReference blockReference, Block oldBlock, int oldMeta, Block newBlock, int newMeta, Area area) {
    super(false, blockReference);
    this.oldBlock = oldBlock;
    this.oldMeta = oldMeta;
    this.newBlock = newBlock;
    this.newMeta = newMeta;
    this.area = area;
  }

  public Block getOldBlock() {
    return oldBlock;
  }

  public int getOldMeta() {
    return oldMeta;
  }

  public Block getNewBlock() {
    return newBlock;
  }

  public int getNewMeta() {
    return newMeta;
  }

  public Area getArea() {
    return area;
  }
}

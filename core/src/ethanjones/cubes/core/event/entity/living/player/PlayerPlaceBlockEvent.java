package ethanjones.cubes.core.event.entity.living.player;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.entity.living.player.Player;
import ethanjones.cubes.world.collision.BlockIntersection;
import ethanjones.cubes.world.reference.BlockReference;

public class PlayerPlaceBlockEvent extends PlayerEvent {

  private final Block block;
  private int meta;
  private final BlockIntersection blockIntersection;
  private final BlockReference blockReference;

  public PlayerPlaceBlockEvent(Player player, Block block, int meta, BlockIntersection blockIntersection, BlockReference blockReference) {
    super(player, true);
    this.block = block;
    this.meta = meta;
    this.blockIntersection = blockIntersection;
    this.blockReference = blockReference;
  }

  public Block getBlock() {
    return block;
  }

  public BlockIntersection getBlockIntersection() {
    return blockIntersection;
  }

  public BlockReference getBlockReference() {
    return blockReference;
  }

  public int getMeta() {
    return meta;
  }

  public void setMeta(int meta) {
    this.meta = meta;
  }
}

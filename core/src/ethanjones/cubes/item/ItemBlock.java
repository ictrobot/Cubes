package ethanjones.cubes.item;

import ethanjones.cubes.block.Block;
import ethanjones.cubes.core.util.BlockFace;

public class ItemBlock extends Item {
  private final Block block;

  public ItemBlock(Block block) {
    super(block.id);
    this.block = block;
  }

  public void loadGraphics() {
    this.texture = block.getTextureHandler().getSide(BlockFace.posX);
  }
}

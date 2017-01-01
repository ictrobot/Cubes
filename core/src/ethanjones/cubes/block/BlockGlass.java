package ethanjones.cubes.block;

import ethanjones.cubes.core.util.BlockFace;
import ethanjones.cubes.item.ItemTool.ToolType;

public class BlockGlass extends Block {
  
  public BlockGlass() {
    super("core:glass");
    miningTime = 0.25f;
    miningTool = ToolType.none;
    miningOther = false;
  }
  
  @Override
  public boolean renderFace(BlockFace blockFace, int neighbourIDAndMeta) {
    return (neighbourIDAndMeta & 0xFFFFF) != intID && super.renderFace(blockFace, neighbourIDAndMeta);
  }
  
  @Override
  public boolean alwaysTransparent() {
    return true;
  }
}

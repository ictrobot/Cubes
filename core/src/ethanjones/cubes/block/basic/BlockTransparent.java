package ethanjones.cubes.block.basic;

import ethanjones.cubes.block.Block;

public class BlockTransparent extends Block {

  public BlockTransparent() {
    super("core:glass");
  }

  public BlockTransparent(String id) {
    super(id);
  }

  @Override
  public boolean isTransparent() {
    return true;
  }
}

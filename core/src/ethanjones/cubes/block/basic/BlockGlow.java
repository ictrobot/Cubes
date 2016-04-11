package ethanjones.cubes.block.basic;

import ethanjones.cubes.block.Block;

public class BlockGlow extends Block {
  public BlockGlow() {
    super("core:glow");
  }

  @Override
  public int getLightLevel() {
    return 15;
  }
}

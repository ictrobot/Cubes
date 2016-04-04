package ethanjones.cubes.block.basic;

import ethanjones.cubes.block.Block;

public class BlockLight extends Block {
  public BlockLight() {
    super("core:light");
  }

  @Override
  public int getLightLevel() {
    return 15;
  }
}

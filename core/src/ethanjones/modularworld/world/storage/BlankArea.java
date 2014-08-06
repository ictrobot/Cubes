package ethanjones.modularworld.world.storage;

import ethanjones.modularworld.block.factory.BlockFactory;

public class BlankArea extends Area {

  public BlankArea() {
    this(Integer.MAX_VALUE / Area.SIZE_BLOCKS);
  }

  private BlankArea(int value) {
    super(value, value, value, false);
  }

  @Override
  public BlockFactory getBlockFactory(int x, int y, int z) {
    return null;
  }

  @Override
  public void setBlockFactory(BlockFactory blockFactory, int x, int y, int z) {

  }

}

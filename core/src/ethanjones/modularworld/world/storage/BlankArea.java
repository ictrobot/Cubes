package ethanjones.modularworld.world.storage;

import ethanjones.data.DataGroup;
import ethanjones.modularworld.block.Block;
import ethanjones.modularworld.networking.packets.PacketBlockChanged;

public class BlankArea extends Area {

  public BlankArea() {
    this(Integer.MAX_VALUE / Area.SIZE_BLOCKS);
  }

  private BlankArea(int value) {
    super(value, value, value, false);
  }

  @Override
  public Block getBlock(int x, int y, int z) {
    return null;
  }

  @Override
  public void setBlock(Block block, int x, int y, int z, boolean event) {

  }

  @Override
  public void read(DataGroup data) {

  }

  @Override
  public DataGroup write() {
    return new DataGroup();
  }

  @Override
  public void handleChange(PacketBlockChanged packet) {

  }
}

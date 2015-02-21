package ethanjones.cubes.common.block;

public enum BlockFace {
  posX,
  negX,
  posY,
  negY,
  posZ,
  negZ;

  public int index;

  private BlockFace() {
    this.index = ordinal();
  }
}

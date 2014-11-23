package ethanjones.cubes.core.util;

public enum BlockFace {
  posX(0),
  negX(1),
  posY(2),
  negY(3),
  posZ(4),
  negZ(5);

  public int index;

  private BlockFace(int index) {
    this.index = index;
  }
}

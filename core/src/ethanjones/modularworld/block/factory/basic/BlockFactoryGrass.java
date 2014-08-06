package ethanjones.modularworld.block.factory.basic;

import ethanjones.modularworld.core.util.Direction;

public class BlockFactoryGrass extends BlockFactoryBasic {
  public BlockFactoryGrass() {
    super("Grass - Side");
  }

  public void loadGraphics() {
    super.loadGraphics();
    this.textureHandler.setSide(Direction.posY, "Grass").setSide(Direction.negY, "Dirt");
  }
}
